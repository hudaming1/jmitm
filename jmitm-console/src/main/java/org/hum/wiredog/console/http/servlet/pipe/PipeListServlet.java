package org.hum.wiredog.console.http.servlet.pipe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiredog.console.http.service.PipeService;
import org.hum.wiredog.console.http.vo.WiredogPipeListQueryVO;

import com.alibaba.fastjson.JSON;

/**
 * http://localhost:8080/pipe/list
 */
public class PipeListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private PipeService pipeService = new PipeService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Content-Type", "application/json");
		WiredogPipeListQueryVO queryVo = new WiredogPipeListQueryVO();
		String isActive = req.getParameter("active") ;
		if (isActive != null) {
			queryVo.setActive(Boolean.parseBoolean(isActive));
		}
		resp.getWriter().print(JSON.toJSONString(pipeService.list(queryVo)));
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
