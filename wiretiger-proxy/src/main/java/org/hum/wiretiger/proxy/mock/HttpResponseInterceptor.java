package org.hum.wiretiger.proxy.mock;

import io.netty.handler.codec.http.FullHttpResponse;

public interface HttpResponseInterceptor {

	public boolean eval(FullHttpResponse resp);
}
