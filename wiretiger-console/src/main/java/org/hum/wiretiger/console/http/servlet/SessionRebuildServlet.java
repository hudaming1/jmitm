package org.hum.wiretiger.console.http.servlet;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiretiger.common.constant.HttpConstant;
import org.hum.wiretiger.console.common.WtSession;
import org.hum.wiretiger.console.http.service.SessionService;

/**
 * http://localhost:8080/session/rebuild
 */
public class SessionRebuildServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final SessionService sessionService = new SessionService();
	private final String RETURN_LINE = "\r\n";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Content-Type", "text/plain; charset=utf-8");
		String requestBody = Objects.toString(req.getParameter("body"), "");
		WtSession wtSession = sessionService.getWtSessionById(Long.parseLong(req.getParameter("id")));
		wtSession.getRequest().headers().set(HttpConstant.ContentLength, requestBody.getBytes().length);
		String requestHeaderAndLine = sessionService.convert2RequestHeaderAndLine(wtSession, RETURN_LINE);
		resp.getWriter().print(requestHeaderAndLine + RETURN_LINE + requestBody + RETURN_LINE);
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
