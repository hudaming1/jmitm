package org.hum.wiretiger.proxy.mock;

import org.hum.wiretiger.proxy.mock.netty.NettyResponseInterceptor;
import org.hum.wiretiger.proxy.mock.netty.NettyResponseRebuild;
import org.hum.wiretiger.proxy.mock.wiretiger.HttpResponse;
import org.hum.wiretiger.proxy.mock.wiretiger.HttpResponseInterceptor;
import org.hum.wiretiger.proxy.mock.wiretiger.HttpResponseRebuild;

import io.netty.handler.codec.http.FullHttpResponse;

public class CatchResponse {
	
	private NettyResponseInterceptor responseInterceptor;
	private NettyResponseRebuild responseRebuild;
	
	public CatchResponse eval(HttpResponseInterceptor responseInterceptor) {
		this.responseInterceptor = new NettyResponseInterceptor() {
			@Override
			public final boolean isHit(FullHttpResponse response) {
				return responseInterceptor.isHit(new HttpResponse(response));
			}
		};
		return this;
	}

	public CatchResponse evalNettyResponse(NettyResponseInterceptor responseInterceptor) {
		this.responseInterceptor = responseInterceptor;
		return this;
	}

	public CatchResponse rebuildResponse(HttpResponseRebuild responseRebuild) {
		this.responseRebuild = new NettyResponseRebuild() {
			@Override
			public final FullHttpResponse eval(FullHttpResponse response) {
				return responseRebuild.eval(new HttpResponse(response)).toFullHttpResponse();
			}
		};
		return this;
	}

	public CatchResponse rebuildNettyResponse(NettyResponseRebuild responseRebuild) {
		this.responseRebuild = responseRebuild;
		return this;
	}

	public Mock mock() {
		return new Mock(null, responseInterceptor, null, responseRebuild);
	}

}
