package org.hum.wiretiger.proxy.mock;

import java.util.List;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

public class MockHandler {
	
	private final String WT_MOCK_SIGN = "::WtMock";
	private List<Mock> mockList;
	
	public MockHandler(List<Mock> mocks) {
		this.mockList = mocks;
	}

	public void mock(HttpRequest request, FullHttpResponse resp) {
		for (Mock mock : mockList) {
		}
	}

	public void mock(HttpRequest request) {
		for (Mock mock : mockList) {
		}
	}

}
