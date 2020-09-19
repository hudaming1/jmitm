package org.hum.wiretiger.proxy.mock;

import io.netty.handler.codec.http.FullHttpRequest;

public interface RequestRebuilder {

	public FullHttpRequest eval(FullHttpRequest request);
}
