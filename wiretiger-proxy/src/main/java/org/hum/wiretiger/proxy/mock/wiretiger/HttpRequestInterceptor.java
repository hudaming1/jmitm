package org.hum.wiretiger.proxy.mock.wiretiger;

import org.hum.wiretiger.proxy.mock.netty.NettyRequestInterceptor;

import io.netty.handler.codec.http.FullHttpRequest;

public abstract class HttpRequestInterceptor implements NettyRequestInterceptor {
	
	public abstract boolean isHit(HttpRequest request);

	public final boolean isHit(FullHttpRequest request) {
		return isHit(new HttpRequest(request));
	}
}
