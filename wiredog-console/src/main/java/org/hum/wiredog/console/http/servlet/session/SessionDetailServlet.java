package org.hum.wiredog.console.http.servlet.session;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiredog.console.http.service.SessionService;

import com.alibaba.fastjson.JSON;

/**
 * http://localhost:8080/session/get
 */
public class SessionDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final SessionService sessionService = new SessionService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Content-Type", "application/json");
		resp.getWriter().print(JSON.toJSONString(sessionService.getById(Long.parseLong(req.getParameter("id")))));
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
