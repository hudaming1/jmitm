package org.hum.wiretiger.proxy.mock.wiretiger;

import org.hum.wiretiger.proxy.mock.netty.NettyRequestRebuilder;

import io.netty.handler.codec.http.FullHttpRequest;

public abstract class HttpRequestRebuilder implements NettyRequestRebuilder {
	
	public abstract HttpRequest eval(HttpRequest request);

	@Override
	public final FullHttpRequest eval(FullHttpRequest request) {
		return this.eval(new HttpRequest(request)).toFullHttpRequest();
	}
}
