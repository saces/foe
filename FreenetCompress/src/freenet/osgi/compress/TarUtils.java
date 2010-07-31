package freenet.osgi.compress;

import org.apache.commons.compress.archivers.tar.TarConstants;

public class TarUtils {

	private TarUtils() {
	}

	static long paxFormatter(StringBuilder out, String item, long value) {
		return paxFormatter(out, item, Long.toString(value));
	}

	static long paxFormatter(StringBuilder out, String item, String value) {
		StringBuilder sb = new StringBuilder(3 + item.length() + value.length());
		sb.append(' ');
		sb.append(item);
		sb.append('=');
		sb.append(value);
		sb.append('\n');
		byte[] data = sb.toString().getBytes();
		int l = stringSize(data.length);
		long len = data.length;
		len += l;
		if (stringSize(len) > l) {
			len++;
		}
		out.append(len);
		out.append(sb);
		return len;
	}

	static int stringSize(long x) {
		long p = 10;
		for (int i = 1; i < 19; i++) {
			if (x < p)
				return i;
			p = 10 * p;
		}
		return 19;
	}

	static boolean needsPaxingName(String name) {
		return needsPaxing(name, TarConstants.NAMELEN);
	}

	static boolean needsPaxing(String value, long maxlength) {
		byte[] bytes = value.getBytes();
		if (bytes.length > maxlength) {
			return true;
		}
		for (byte b : bytes) {
			if (b < 1) {
				return true;
			}
		}
		return false;
	}

	static boolean needsPaxingSize(long size) {
		return needsPaxing(size, TarConstants.MAXSIZE);
	}

	static boolean needsPaxing(long value, long maxvalue) {
		return value > maxvalue;
	}

}
