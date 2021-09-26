package org.hum.jmitm.proxy.mock.netty;

import io.netty.handler.codec.http.FullHttpResponse;

public interface NettyResponseInterceptor {

	public boolean isHit(FullHttpResponse response);
}
