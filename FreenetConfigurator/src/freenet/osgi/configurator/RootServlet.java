package freenet.osgi.configurator;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RootServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

//	public void destroy() {
//		new Error("destroy").printStackTrace();
//	}
//
//	public ServletConfig getServletConfig() {
//		new Error("getServletConfig").printStackTrace();
//		return null;
//	}
//
//	public String getServletInfo() {
//		new Error("getServletInfo").printStackTrace();
//		return null;
//	}
//
//	public void init(ServletConfig arg0) throws ServletException {
//		new Error("init").printStackTrace();
//	}
//
//	public void service(ServletRequest arg0, ServletResponse arg1)
//			throws ServletException, IOException {
//		new Error("service").printStackTrace();
//	}

}
