package org.hum.wiretiger.proxy.mock.rebuild;

import io.netty.handler.codec.http.FullHttpResponse;

public interface ResponseRebuild {

	public FullHttpResponse eval(FullHttpResponse response);
}
