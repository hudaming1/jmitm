package org.hum.wiretiger.console.http.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiretiger.console.common.WtSession;
import org.hum.wiretiger.console.http.service.SessionService;

/**
 * http://localhost:8080/session/getHttpRequest
 */
public class CopyHttpRequestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final SessionService sessionService = new SessionService();
	private final String RETURN_LINE = "\r\n";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Content-Type", "text/plain");
		WtSession wtSession = sessionService.getWtSessionById(Long.parseLong(req.getParameter("id")));
		String requestHeaderAndLine = sessionService.convert2RequestHeaderAndLine(wtSession, RETURN_LINE);
		String body = wtSession.getRequestBytes() == null ? "" : new String(wtSession.getRequestBytes());
		resp.getWriter().print(requestHeaderAndLine + RETURN_LINE + body + RETURN_LINE);
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
