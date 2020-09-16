package org.hum.wiretiger.proxy.mock;

import java.util.List;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

public class MockHandler {
	
	private final String WT_MOCK_SIGN = "_WtMock";
	private List<Mock> mockList;
	
	public MockHandler(List<Mock> mocks) {
		this.mockList = mocks;
	}

	public void mock(HttpRequest request) {
		if (mockList == null || mockList.isEmpty()) {
			return ;
		}
		
		for (Mock mock : mockList) {
			if (mock.getRequestInterceptor() != null && mock.getRequestInterceptor().isHit(request)) {
				if (mock.getRequestRebuilder() != null) {
					request = mock.getRequestRebuilder().eval(request);
					request.headers().set(WT_MOCK_SIGN, mock.getId());
				}
			}
		}
	}

	public void mock(HttpRequest request, FullHttpResponse resp) {
		if (mockList == null || mockList.isEmpty()) {
			return ;
		}
		
		for (Mock mock : mockList) {
			boolean requestMiss = request.headers().contains(WT_MOCK_SIGN) == false;
			if (requestMiss && mock.getResponseInterceptor() == null || !mock.getResponseInterceptor().isHit(resp)) {
				continue;
			}
			if (mock.getResponseRebuild() != null) {
				resp = mock.getResponseRebuild().eval(resp);
				resp.headers().set(WT_MOCK_SIGN, mock.getId());
			}
		}
	}

}
