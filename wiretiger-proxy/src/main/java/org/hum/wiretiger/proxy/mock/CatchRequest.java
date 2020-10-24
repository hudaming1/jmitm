package org.hum.wiretiger.proxy.mock;

import org.hum.wiretiger.proxy.mock.netty.NettyRequestInterceptor;
import org.hum.wiretiger.proxy.mock.netty.NettyRequestRebuilder;
import org.hum.wiretiger.proxy.mock.netty.NettyResponseRebuild;
import org.hum.wiretiger.proxy.mock.wiretiger.HttpRequestInterceptor;
import org.hum.wiretiger.proxy.mock.wiretiger.HttpRequestRebuilder;
import org.hum.wiretiger.proxy.mock.wiretiger.HttpResponseRebuild;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;

@Data
public class CatchRequest {
	
	private NettyRequestInterceptor requestInterceptor;
	private NettyRequestRebuilder requestRebuilder;
	private NettyResponseRebuild responseRebuild;
	
	public CatchRequest eval(HttpRequestInterceptor requestInterceptor) {
		this.requestInterceptor = new NettyRequestInterceptor() {
			@Override
			public boolean isHit(FullHttpRequest request) {
				return requestInterceptor.isHit(request);
			}
		};
		return this;
	}

	public CatchRequest evalNettyRequest(NettyRequestInterceptor requestInterceptor) {
		this.requestInterceptor = requestInterceptor;
		return this;
	}

	public CatchRequest rebuildRequest(HttpRequestRebuilder requestRebuilder) {
		this.requestRebuilder = requestRebuilder;
		return this;
	}

	public CatchRequest rebuildNettyRequest(NettyRequestRebuilder requestRebuilder) {
		this.requestRebuilder = requestRebuilder;
		return this;
	}

	public CatchRequest rebuildResponse(HttpResponseRebuild responseRebuild) {
		this.responseRebuild = responseRebuild;
		return this;
	}

	public CatchRequest rebuildNettyResponse(NettyResponseRebuild responseRebuild) {
		this.responseRebuild = responseRebuild;
		return this;
	}

	public Mock mock() {
		return new Mock(requestInterceptor, null, requestRebuilder, responseRebuild);
	}
}
