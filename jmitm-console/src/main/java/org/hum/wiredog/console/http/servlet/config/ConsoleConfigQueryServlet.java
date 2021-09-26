package org.hum.wiredog.console.http.servlet.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiredog.console.http.vo.config.ConsoleConfigVO;
import org.hum.wiredog.proxy.config.WiredogCoreConfigProvider;

import com.alibaba.fastjson.JSON;

/**
 * http://localhost:8080/config/get
 */
public class ConsoleConfigQueryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().print(JSON.toJSONString(new ConsoleConfigVO(WiredogCoreConfigProvider.get().isParseHttps(), WiredogCoreConfigProvider.get().isOpenMasterMockStwich())));
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
