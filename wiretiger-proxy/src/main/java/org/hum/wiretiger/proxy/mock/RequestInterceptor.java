package org.hum.wiretiger.proxy.mock;

import io.netty.handler.codec.http.FullHttpRequest;

public interface RequestInterceptor {

	public boolean isHit(FullHttpRequest request);
}
