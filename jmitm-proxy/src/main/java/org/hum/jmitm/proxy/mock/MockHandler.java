package org.hum.jmitm.proxy.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hum.jmitm.proxy.mock.netty.NettyResponseInterceptor;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class MockHandler {
	
	private final String WT_MOCK_SIGN = "_WtMock";
	private List<Mock> mockList;
	
	public MockHandler(List<Mock> mocks) {
		this.mockList = mocks;
	}

	public FullHttpResponse mock(FullHttpRequest request) {
		if (mockList == null || mockList.isEmpty()) {
			return null;
		}
		
		// rebuild-request
		for (Mock mock : mockList) {
			if (mock.status() == MockStatus.Disabled) {
				continue;
			} else if (mock.getRequestInterceptor() != null && mock.getRequestInterceptor().isHit(request)) {
				// 标记Header被拦截过(标记格式：#mockId1,#mockId2,#mockId3....)
				String mockIdenArray = !request.headers().contains(WT_MOCK_SIGN) ? mock.getId() + "," : request.headers().get(WT_MOCK_SIGN) + "," + mock.getId() + ",";
				request.headers().set(WT_MOCK_SIGN, mockIdenArray.substring(0, mockIdenArray.length() - 1));
				if (mock.getRequestRebuilder() != null) {
					request = mock.getRequestRebuilder().eval(request);
				}
			}
		}
		// mock (在Mock响应前，要保证获取到最终Rebuild过的Request)
		for (Mock mock : mockList) {
			if (mock.status() == MockStatus.Disabled) {
				continue;
			} else if (mock.getRequestInterceptor() != null && mock.getRequestInterceptor().isHit(request)) {
				// 标记Header被拦截过
				String mockIden = request.headers().get(WT_MOCK_SIGN) + "," + mock.getId();
				request.headers().set(WT_MOCK_SIGN, mockIden.substring(0, mockIden.length() - 1));
				// mock
				if (mock.getHttpMockResponse() != null) {
					return mock.getHttpMockResponse().eval(request);
				}
			}
		}
		return null;
	}
	
	public void mock(FullHttpRequest request, FullHttpResponse resp) {
		if (mockList == null || mockList.isEmpty()) {
			return ;
		}
		
		// 获取请求命中Mock时种下的Header
		String mockIds = request.headers().get(WT_MOCK_SIGN);
		Set<String> mockIdSet = mockIds == null ? Collections.emptySet() : new HashSet<>(Arrays.asList(mockIds.split(",")));
		
		for (Mock mock : mockList) {
			boolean requestMiss = request.headers().contains(WT_MOCK_SIGN) == false || !mockIdSet.contains(mock.getId());
			NettyResponseInterceptor responseInterceptor = mock.getResponseInterceptor();
			// 如果Request和Response都没有命中，则返回
			if (requestMiss && (responseInterceptor == null || !responseInterceptor.isHit(resp))) {
				continue;
			}
			if (mock.getResponseRebuild() != null) {
				resp = mock.getResponseRebuild().eval(resp);
				resp.headers().set(WT_MOCK_SIGN, mock.getId());
			}
		}
		
		// TODO 性能待优化：真实场景大部分请求都是不被命中，且ResponseBuilder也没有配置，因此这里是不用遍历每一个Mock去判断，是可以提前退出的
	}

}
