package org.hum.wiretiger.proxy.mock.wiretiger;

import org.hum.wiretiger.proxy.mock.netty.NettyResponseRebuild;

import io.netty.handler.codec.http.FullHttpResponse;

public abstract class HttpResponseRebuild implements NettyResponseRebuild {

	public abstract HttpResponse eval(HttpResponse resp);
	
	@Override
	public final FullHttpResponse eval(FullHttpResponse response) {
		return eval(new HttpResponse(response)).toFullHttpResponse();
	}
}
