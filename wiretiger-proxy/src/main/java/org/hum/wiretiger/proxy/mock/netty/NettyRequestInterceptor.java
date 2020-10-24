package org.hum.wiretiger.proxy.mock.netty;

import io.netty.handler.codec.http.FullHttpRequest;

public interface NettyRequestInterceptor {

	public boolean isHit(FullHttpRequest request);
}
