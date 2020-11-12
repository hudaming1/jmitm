package org.hum.wiredog.console.http.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiredog.proxy.config.WiredogCoreConfigProvider;

/**
 * http://localhost:8080/mock/switch
 */
public class MockMasterSwitchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 传入1位开启开关；其余包括null全部认为关闭Mock
		WiredogCoreConfigProvider.get().setOpenMasterMockStwich("true".equals(req.getParameter("switcher")));
		resp.getWriter().print("true");
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
