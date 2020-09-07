package org.hum.wiretiger.console.http.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiretiger.console.http.service.SessionService;
import org.hum.wiretiger.console.http.vo.WiretigerSessionListQueryVO;

import com.alibaba.fastjson.JSON;

/**
 * http://localhost:8080/session/list
 */
public class SessionListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private SessionService sessionService = new SessionService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pipeId = req.getParameter("pipe_id");
		
		WiretigerSessionListQueryVO query = new WiretigerSessionListQueryVO();
		query.setPipeId(pipeId == null ? null : Long.parseLong(pipeId));
		query.setKeyword(req.getParameter("keyword"));
		
		resp.setHeader("Content-Type", "application/json");
		resp.getWriter().print(JSON.toJSONString(sessionService.list(query)));
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
