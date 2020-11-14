package org.hum.wiredog.console.http.servlet.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hum.wiredog.console.http.vo.MockListVO;
import org.hum.wiredog.console.http.vo.ServletResult;
import org.hum.wiredog.proxy.config.WiredogCoreConfigProvider;
import org.hum.wiredog.proxy.mock.Mock;

import com.alibaba.fastjson.JSON;

/**
 * http://localhost:8080/config/mock/list
 */
public class MockListQueryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		List<Mock> mockList = WiredogCoreConfigProvider.get().getMockList();
		List<MockListVO> mockVoList = new ArrayList<MockListVO>();
		if (mockList != null && !mockList.isEmpty()) {
			mockList.forEach(item -> {
				mockVoList.add(new MockListVO(item.getId(), item.getName(), item.getDesc(), item.getStatus().getName()));
			});
		}
		resp.getWriter().print(JSON.toJSONString(new ServletResult<List<MockListVO>>(mockVoList)));
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}
