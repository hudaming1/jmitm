package org.hum.wiretiger.proxy.mock.netty;

import io.netty.handler.codec.http.FullHttpResponse;

public interface NettyResponseRebuild {

	public FullHttpResponse eval(FullHttpResponse response);
}
