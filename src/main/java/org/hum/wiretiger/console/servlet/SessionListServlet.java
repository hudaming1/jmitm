package org.hum.wiretiger.console.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiretiger.console.service.SessionService;
import org.hum.wiretiger.console.vo.WtSessionListQueryVO;

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
		
		WtSessionListQueryVO query = new WtSessionListQueryVO();
		query.setPipeId(pipeId == null ? null : Integer.parseInt(pipeId));
		
		resp.setHeader("Content-Type", "application/json");
		resp.getWriter().print(JSON.toJSONString(sessionService.list(query)));
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
