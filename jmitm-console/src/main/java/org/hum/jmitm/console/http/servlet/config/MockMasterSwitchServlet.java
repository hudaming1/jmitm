package org.hum.jmitm.console.http.servlet.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.jmitm.proxy.config.JmitmCoreConfigProvider;

/**
 * http://localhost:8080/config/mock_update
 */
public class MockMasterSwitchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JmitmCoreConfigProvider.get().setOpenMasterMockStwich("true".equals(req.getParameter("switcher")));
		resp.getWriter().print("true");
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
