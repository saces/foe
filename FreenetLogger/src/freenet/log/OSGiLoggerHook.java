package freenet.log;

import java.io.Closeable;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.osgi.service.log.LogService;

/**
 * Converted the old StandardLogger to Ian's loggerhook interface.
 * 
 * @author oskar
 */
public class OSGiLoggerHook extends LoggerHook implements Closeable {
	
	private final LogService osgiLog;

	/** Verbosity types */
	public static final int DATE = 1,
		CLASS = 2,
		HASHCODE = 3,
		THREAD = 4,
		PRIORITY = 5,
		MESSAGE = 6,
		UNAME = 7;

	private volatile boolean closed = false;

	/** Name of the local host (called uname in Unix-like operating systems). */
	private static String uname;
	static {
		uname = "unknown";
	}

	static synchronized final void getUName() {
		if(!uname.equals("unknown")) return;
		System.out.println("Getting uname for logging");
		try {
			InetAddress addr = InetAddress.getLocalHost();
			if (addr != null) {
				uname =
					new StringTokenizer(addr.getHostName(), ".").nextToken();
			}
		} catch (Exception e) {
			// Ignored.
		}
	}
	
	private DateFormat df;
	private int[] fmt;
	private String[] str;

	private Date myDate = new Date();

	/**
	 * Create a Logger to send log output to the given PrintStream.
	 * 
	 * @param stream
	 *            the PrintStream to send log output to.
	 * @param fmt
	 *            log message format string
	 * @param dfmt
	 *            date format string
	 * @param threshold
	 *            Lowest logged priority
	 */
	public OSGiLoggerHook(LogService osgilog, String fmt, String dfmt, int threshold) {
		super(threshold);
		osgiLog = osgilog;
		setDateFormat(dfmt);
		setLogFormat(fmt);
	}

	private void setLogFormat(String fmt) {
		if ((fmt == null) || (fmt.length() == 0))
			fmt = "d:c:h:t:p:m";
		char[] f = fmt.toCharArray();

		ArrayList<Integer> fmtVec = new ArrayList<Integer>();
		ArrayList<String> strVec = new ArrayList<String>();

		StringBuilder sb = new StringBuilder();

		boolean comment = false;
		for (int i = 0; i < f.length; ++i) {
			int type = numberOf(f[i]);
			if(type == UNAME)
				getUName();
			if (!comment && (type != 0)) {
				if (sb.length() > 0) {
					strVec.add(sb.toString());
					fmtVec.add(0);
					sb = new StringBuilder();
				}
				fmtVec.add(type);
			} else if (f[i] == '\\') {
				comment = true;
			} else {
				comment = false;
				sb.append(f[i]);
			}
		}
		if (sb.length() > 0) {
			strVec.add(sb.toString());
			fmtVec.add(0);
		}

		this.fmt = new int[fmtVec.size()];
		int size = fmtVec.size();
		for (int i = 0; i < size; ++i)
			this.fmt[i] = fmtVec.get(i);

		this.str = new String[strVec.size()];
		str = strVec.toArray(str);
	}

	private void setDateFormat(String dfmt) {
		if ((dfmt != null) && (dfmt.length() != 0)) {
			try {
				df = new SimpleDateFormat(dfmt);
			} catch (RuntimeException e) {
				df = DateFormat.getDateTimeInstance();
			}
		} else
			df = DateFormat.getDateTimeInstance();

		df.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public void log(Object o, Class<?> c, String msg, Throwable e, int priority) {
		if (!instanceShouldLog(priority, c))
			return;

		if (closed)
			return;
		
		StringBuilder sb = new StringBuilder( e == null ? 512 : 1024 );
		int sctr = 0;

		for (int i = 0; i < fmt.length; ++i) {
			switch (fmt[i]) {
				case 0 :
					sb.append(str[sctr++]);
					break;
				case DATE :
					long now = System.currentTimeMillis();
					synchronized (this) {
						myDate.setTime(now);
						sb.append(df.format(myDate));
					}
					break;
				case CLASS :
					sb.append(c == null ? "<none>" : c.getName());
					break;
				case HASHCODE :
					sb.append(
						o == null
							? "<none>"
							: Integer.toHexString(o.hashCode()));
					break;
				case THREAD :
					sb.append(Thread.currentThread().getName());
					break;
				case PRIORITY :
					sb.append(LoggerHook.priorityOf(priority));
					break;
				case MESSAGE :
					sb.append(msg);
					break;
				case UNAME :
					sb.append(uname);
					break;
			}
		}
		sb.append('\n');

		priority = transformLogLevelFO(priority);
		if (e != null) {
			osgiLog.log(priority, sb.toString(), e);
		} else {
			osgiLog.log(priority, sb.toString());
		}
		
	}

	private static int numberOf(char c) {
		switch (c) {
			case 'd' :
				return DATE;
			case 'c' :
				return CLASS;
			case 'h' :
				return HASHCODE;
			case 't' :
				return THREAD;
			case 'p' :
				return PRIORITY;
			case 'm' :
				return MESSAGE;
			case 'u' :
				return UNAME;
			default :
				return 0;
		}
	}

	@Override
	public long minFlags() {
		return 0;
	}

	@Override
	public long notFlags() {
		return INTERNAL;
	}

	@Override
	public long anyFlags() {
		return ((2 * ERROR) - 1) & ~(threshold - 1);
	}

	public void close() {
		closed = true;
	}

}
