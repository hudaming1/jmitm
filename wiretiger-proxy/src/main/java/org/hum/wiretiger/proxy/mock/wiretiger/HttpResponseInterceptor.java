package org.hum.wiretiger.proxy.mock.wiretiger;

import org.hum.wiretiger.proxy.mock.netty.NettyResponseInterceptor;

import io.netty.handler.codec.http.FullHttpResponse;

public abstract class HttpResponseInterceptor implements NettyResponseInterceptor {
	
	public abstract boolean isHit(HttpResponse response);

	@Override
	public final boolean isHit(FullHttpResponse response) {
		return isHit(new HttpResponse(response));
	}
}
