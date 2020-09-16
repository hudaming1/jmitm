package org.hum.wiretiger.proxy.mock;

import io.netty.handler.codec.http.HttpRequest;

public interface RequestInterceptor {

	public boolean isHit(HttpRequest request);
}
