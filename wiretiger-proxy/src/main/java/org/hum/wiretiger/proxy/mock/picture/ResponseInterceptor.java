package org.hum.wiretiger.proxy.mock.picture;

import io.netty.handler.codec.http.FullHttpResponse;

public interface ResponseInterceptor {

	public boolean isHit(FullHttpResponse response);
}
