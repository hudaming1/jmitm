package org.hum.jmitm.console.http.servlet.config;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.jmitm.console.http.vo.ServletResult;
import org.hum.jmitm.proxy.config.WiredogCoreConfigProvider;
import org.hum.jmitm.proxy.mock.Mock;
import org.hum.jmitm.proxy.mock.MockStatus;

import com.alibaba.fastjson.JSON;

/**
 * http://localhost:8080/config/mock/update
 */
public class MockUpdatorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String mockId = req.getParameter("id");
		String mockStatus = req.getParameter("status");
		
		List<Mock> mockList = WiredogCoreConfigProvider.get().getMockList();
		if (mockList != null && !mockList.isEmpty()) {
			mockList.forEach(mock -> {
				if (mock.getId().equals(mockId)) {
					mock.status(MockStatus.getEnum(Integer.parseInt(mockStatus)));
				}
			});
		}
		
		resp.getWriter().print(JSON.toJSONString(new ServletResult<Boolean>(true)));
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
