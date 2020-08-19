package org.hum.wiretiger.console.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiretiger.console.service.ConnectionService;
import org.hum.wiretiger.console.vo.WtRequestListQueryVO;

import com.alibaba.fastjson.JSON;

/**
 * http://localhost:8080/request/list
 */
public class RequestListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private ConnectionService connectionService = new ConnectionService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pipeId = req.getParameter("pipe_id");
		
		WtRequestListQueryVO query = new WtRequestListQueryVO();
		query.setPipeId(pipeId == null ? null : Integer.parseInt(pipeId));
		
		resp.setHeader("Content-Type", "application/json");
		resp.getWriter().print(JSON.toJSONString(connectionService.list(query)));
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
