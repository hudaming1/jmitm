package org.hum.wiretiger.proxy.mock.picture;

import io.netty.handler.codec.http.HttpRequest;

public interface RequestInterceptor {

	public boolean eval(HttpRequest request);
}
