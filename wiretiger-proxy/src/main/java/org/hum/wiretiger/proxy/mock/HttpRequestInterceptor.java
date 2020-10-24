package org.hum.wiretiger.proxy.mock;

import io.netty.handler.codec.http.FullHttpRequest;

public abstract class HttpRequestInterceptor implements RequestInterceptor {
	
	public abstract boolean isHit(HttpRequest request);

	public boolean isHit(FullHttpRequest request) {
		return isHit(new HttpRequest(request));
	}
}
