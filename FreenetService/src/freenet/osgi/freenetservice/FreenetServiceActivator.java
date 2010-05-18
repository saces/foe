package freenet.osgi.freenetservice;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import freenet.log.Logger;
import freenet.log.LoggerHook;
import freenet.log.OSGiLoggerHook;
import freenet.node.NodeStarter;

public class FreenetServiceActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		System.out.println("hallo, freenet...");
		ServiceReference sr = context.getServiceReference(LogService.class.getName());
		LogService ls = null;
		if (sr != null) {
			ls = (LogService) context.getService(sr);
		}

		LoggerHook hook;
		hook = new OSGiLoggerHook(ls,
				"d (c, t, p): m", "MMM dd, yyyy HH:mm:ss:SSS", 0);

		Logger.globalAddHook(hook);

		Logger.error(this, "Hello, Error");
		Logger.normal(this, "Hello, normal");
		Logger.debug(this, "Hello, debug");

		System.out.println("welcome, freenet!");

		NodeStarter.start_osgi(new String[] {});
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("bye, freenet");
		NodeStarter.stop_osgi(0);
	}
}
