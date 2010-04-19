package freenet.osgi.configurator;

import java.util.Dictionary;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

public class Activator implements BundleActivator {
	
	HttpService service;

	public void start(BundleContext context) throws Exception {
		System.out.println("hallo, configurator");
		ServiceReference reference;
		reference = context.getServiceReference(HttpService.class.getName());
		service = (HttpService) context.getService(reference);
		HttpContext httpContext = service.createDefaultHttpContext();
		Dictionary initparams = null;
		service.registerServlet("/", new RootServlet(), initparams, null);
	}

	public void stop(BundleContext context) throws Exception {
		service.unregister("/");
		System.out.println("bye, configurator");
	}
}
