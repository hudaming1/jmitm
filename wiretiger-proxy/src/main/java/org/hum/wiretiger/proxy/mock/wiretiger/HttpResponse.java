package org.hum.wiretiger.proxy.mock.wiretiger;

import io.netty.handler.codec.http.FullHttpResponse;

public class HttpResponse {

	private FullHttpResponse fullResponse;
	
	public HttpResponse(FullHttpResponse fullResponse) {
		this.fullResponse = fullResponse;
	}
	
	public FullHttpResponse toFullHttpResponse() {
		return this.fullResponse;
	}
}
