package org.hum.wiretiger.console.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiretiger.console.service.PipeService;

import com.alibaba.fastjson.JSON;

/**
 * http://localhost:8080/pipe/list
 */
public class PipeDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private PipeService pipeService = new PipeService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().print(JSON.toJSONString(pipeService.getById(Integer.parseInt(req.getParameter("id")))));
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
