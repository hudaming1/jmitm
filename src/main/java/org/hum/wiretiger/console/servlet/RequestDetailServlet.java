package org.hum.wiretiger.console.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiretiger.console.service.RequestService;

import com.alibaba.fastjson.JSON;

/**
 * http://localhost:8080/request/get
 */
public class RequestDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final RequestService requestService = new RequestService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("content-type", "text/html; charset=UTF-8");
		resp.getWriter().print(JSON.toJSONString(requestService.getById(Long.parseLong(req.getParameter("id")))));
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
