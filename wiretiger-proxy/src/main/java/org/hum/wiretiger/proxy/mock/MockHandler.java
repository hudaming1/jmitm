package org.hum.wiretiger.proxy.mock;

import java.util.List;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

public class MockHandler {
	
	private final String WT_MOCK_SIGN = "WtMock";
	private List<Mock> mockList;

	public void mock(HttpRequest request, FullHttpResponse resp) {
		for (Mock mock : mockList) {
			if (resp.headers().contains(WT_MOCK_SIGN) || isHit(mock.getPicture(), resp)) {
				rebuild(mock.getInterceptorRebuilder(), resp);
			}
		}
	}

	public void mock(HttpRequest request) {
		for (Mock mock : mockList) {
			if (isHit(mock.getPicture(), request)) {
				rebuild(mock.getInterceptorRebuilder(), request);
				request.headers().set(WT_MOCK_SIGN, mock.getId());
			}
		}
	}

	private boolean isHit(InterceptorPicture picture, HttpRequest request) {
		// TODO
		
		// checkpoint -> 1.uri
		
		// checkpoint -> 2.header
		
		// checkpoint -> 3.body
		
		
		return false;
	}

	private boolean isHit(InterceptorPicture picture, FullHttpResponse resp) {
		// TODO
		
		// checkpoint -> 1.header
		
		// checkpoint -> 2.body
		
		return false;
	}

	private void rebuild(InterceptorRebuilder interceptorRebuilder, HttpRequest request) {
		// TODO Auto-generated method stub
		
		// rebuild-point -> 1.uri
		// rebuild-point -> 2.header
		// rebuild-point -> 3.body		
	}

	private void rebuild(InterceptorRebuilder interceptorRebuilder, FullHttpResponse resp) {
		// TODO Auto-generated method stub
		// rebuild-point -> 1.header
		// rebuild-point -> 2.body
	}
}
