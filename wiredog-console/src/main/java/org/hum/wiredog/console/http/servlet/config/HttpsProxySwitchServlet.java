package org.hum.wiredog.console.http.servlet.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiredog.proxy.config.WiredogCoreConfigProvider;

/**
 * http://localhost:8080/config/https_proxy_update
 */
public class HttpsProxySwitchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		WiredogCoreConfigProvider.get().setParseHttps("true".equals(req.getParameter("switcher")));
		resp.getWriter().print("true");
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
