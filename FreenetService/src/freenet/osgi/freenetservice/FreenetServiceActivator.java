package freenet.osgi.freenetservice;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import freenet.node.NodeStarter;

public class FreenetServiceActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		System.out.println("hallo, freenet");
		NodeStarter.start_osgi(new String[] {});
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("bye, freenet");
		NodeStarter.stop_osgi(0);
	}
}
