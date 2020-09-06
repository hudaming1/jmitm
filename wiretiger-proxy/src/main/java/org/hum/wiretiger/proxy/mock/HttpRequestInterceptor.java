package org.hum.wiretiger.proxy.mock;

import io.netty.handler.codec.http.HttpRequest;

public interface HttpRequestInterceptor {

	public boolean eval(HttpRequest request);
}
