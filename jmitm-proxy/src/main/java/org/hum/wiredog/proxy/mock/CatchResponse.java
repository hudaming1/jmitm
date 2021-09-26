package org.hum.wiredog.proxy.mock;

import org.hum.wiredog.proxy.mock.netty.NettyResponseInterceptor;
import org.hum.wiredog.proxy.mock.netty.NettyResponseRebuild;
import org.hum.wiredog.proxy.mock.wiredog.HttpResponse;
import org.hum.wiredog.proxy.mock.wiredog.HttpResponseInterceptor;
import org.hum.wiredog.proxy.mock.wiredog.HttpResponseRebuild;

import io.netty.handler.codec.http.FullHttpResponse;

public class CatchResponse {
	
	private NettyResponseInterceptor responseInterceptor;
	private NettyResponseRebuild responseRebuild;

	/**
	 * 拦截具有某一特征的Response
	 * @param responseInterceptor
	 * @return
	 */
	public CatchResponse eval(HttpResponseInterceptor responseInterceptor) {
		this.responseInterceptor = new NettyResponseInterceptor() {
			@Override
			public final boolean isHit(FullHttpResponse response) {
				return responseInterceptor.isHit(new HttpResponse(response));
			}
		};
		return this;
	}

	/**
	 * 拦截具有某一特征的Response(基于NettyResponse)
	 * @param responseInterceptor
	 * @return
	 */
	public CatchResponse evalNettyResponse(NettyResponseInterceptor responseInterceptor) {
		this.responseInterceptor = responseInterceptor;
		return this;
	}

	/**
	 * 重制Response
	 * @param HttpResponseRebuild
	 * @return
	 */
	public CatchResponse rebuildResponse(HttpResponseRebuild responseRebuild) {
		this.responseRebuild = new NettyResponseRebuild() {
			@Override
			public final FullHttpResponse eval(FullHttpResponse response) {
				return responseRebuild.eval(new HttpResponse(response)).toFullHttpResponse();
			}
		};
		return this;
	}

	/**
	 * 重制Response（基于NettyResponse）
	 * @param NettyResponseRebuild
	 * @return
	 */
	public CatchResponse rebuildNettyResponse(NettyResponseRebuild responseRebuild) {
		this.responseRebuild = responseRebuild;
		return this;
	}

	public Mock mock() {
		return new Mock(null, responseInterceptor, null, responseRebuild, null);
	}
}
