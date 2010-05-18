/**
 * 
 */
package freenet.osgi.log;

import org.osgi.service.log.LogService;

/**
 * @author saces
 *
 */
public interface FreenetLogService {

	public static final int LOG_NONE = 0;
	public static final int LOG_ERROR = LogService.LOG_ERROR;
	public static final int LOG_WARNING = LogService.LOG_WARNING;
	public static final int LOG_INFO = LogService.LOG_INFO;
	public static final int LOG_DEBUG = LogService.LOG_DEBUG;

	public static final int LOG_NORMAL = LOG_DEBUG+1;

	public abstract void log(int level, Object source, String message);
	public abstract void log(int level, Object source, String message, Throwable t);

	public abstract void fatal(int returnCode, Object source, String message);
	public abstract void fatal(int returnCode, Object source, String message, Throwable t);

}
