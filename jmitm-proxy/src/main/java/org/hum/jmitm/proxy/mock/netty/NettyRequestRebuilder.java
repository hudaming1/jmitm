package org.hum.jmitm.proxy.mock.netty;

import io.netty.handler.codec.http.FullHttpRequest;

public interface NettyRequestRebuilder {

	public FullHttpRequest eval(FullHttpRequest request);
}
