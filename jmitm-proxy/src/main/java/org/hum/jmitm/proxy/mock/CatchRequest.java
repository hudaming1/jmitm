package org.hum.jmitm.proxy.mock;

import org.hum.jmitm.proxy.mock.codec.HttpRequest;
import org.hum.jmitm.proxy.mock.codec.HttpRequestInterceptor;
import org.hum.jmitm.proxy.mock.codec.HttpRequestRebuilder;
import org.hum.jmitm.proxy.mock.codec.HttpResponse;
import org.hum.jmitm.proxy.mock.codec.HttpResponseMock;
import org.hum.jmitm.proxy.mock.codec.HttpResponseRebuild;
import org.hum.jmitm.proxy.mock.netty.NettyHttpResponseMock;
import org.hum.jmitm.proxy.mock.netty.NettyRequestInterceptor;
import org.hum.jmitm.proxy.mock.netty.NettyRequestRebuilder;
import org.hum.jmitm.proxy.mock.netty.NettyResponseRebuild;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.internal.StringUtil;
import lombok.Data;

@Data
public class CatchRequest {
	
	private NettyRequestInterceptor requestInterceptor;
	private NettyRequestRebuilder requestRebuilder;
	private NettyResponseRebuild responseRebuild;
	private NettyHttpResponseMock responseMock;
	
	/**
	 * 定义拦截具有某一特征的HTTP请求
	 * @param requestInterceptor
	 * @return
	 */
	public CatchRequest eval(HttpRequestInterceptor requestInterceptor) {
		this.requestInterceptor = new NettyRequestInterceptor() {
			@Override
			public boolean isHit(FullHttpRequest request) {
				return requestInterceptor.isHit(new HttpRequest(request));
			}
		};
		return this;
	}

	/**
	 * 定义拦截具有某一特征的HTTP请求（基于原生NettyRequest）
	 * @param NettyRequestInterceptor
	 * @return
	 */
	public CatchRequest evalNettyRequest(NettyRequestInterceptor requestInterceptor) {
		this.requestInterceptor = requestInterceptor;
		return this;
	}

	/**
	 * 重制Request
	 * @param HttpRequestRebuilder
	 * @return
	 */
	public CatchRequest rebuildRequest(HttpRequestRebuilder requestRebuilder) {
		this.requestRebuilder = new NettyRequestRebuilder() {
			@Override
			public final FullHttpRequest eval(FullHttpRequest request) {
				return requestRebuilder.eval(new HttpRequest(request)).toFullHttpRequest();
			}
		};
		return this;
	}

	/**
	 * 重制Request（基于NettyRequest）
	 * @param NettyRequestRebuilder
	 * @return
	 */
	public CatchRequest rebuildNettyRequest(NettyRequestRebuilder requestRebuilder) {
		this.requestRebuilder = requestRebuilder;
		return this;
	}

	/**
	 * 重制Response
	 * @param HttpResponseRebuild
	 * @return
	 */
	public CatchRequest rebuildResponse(HttpResponseRebuild responseRebuild) {
		this.responseRebuild = new NettyResponseRebuild() {
			@Override
			public final FullHttpResponse eval(FullHttpResponse response) {
				return responseRebuild.eval(new HttpResponse(response)).toFullHttpResponse();
			}
		};
		return this;
	}
	
	/**
	 * 根据请求进行Mock响应，不会对对目标服务器发出真正的请求（基于NettyRequest）
	 * @param nettyResponseMock
	 * @return
	 */
	public CatchRequest mockNettyResponse(NettyHttpResponseMock nettyResponseMock) {
		this.responseMock = nettyResponseMock;
		return this;
	}
	
	/**
	 * 根据请求进行Mock响应，不会对对目标服务器发出真正的请求
	 * @param responseMock
	 * @return 返回null代表不做mock，放行请求目标服务器
	 */
	public CatchRequest mockResponse(HttpResponseMock responseMock) {
		this.responseMock = new NettyHttpResponseMock() {
			@Override
			public FullHttpResponse eval(FullHttpRequest request) {
				HttpResponse httpResponse = responseMock.eval(new HttpRequest(request));
				return httpResponse == null ? null : httpResponse.toFullHttpResponse();
			}
		};
		return this;
	}

	/**
	 * 重制Response（基于NettyResponse）
	 * @param NettyResponseRebuild
	 * @return
	 */
	public CatchRequest rebuildNettyResponse(NettyResponseRebuild responseRebuild) {
		this.responseRebuild = responseRebuild;
		return this;
	}

	public Mock mock() {
		return mock(null, null);
	}

	public Mock mock(String name, String desc) {
		Mock mock = new Mock(requestInterceptor, null, requestRebuilder, responseRebuild, responseMock);
		if (!StringUtil.isNullOrEmpty(name)) {
			mock.name(name);
		} else {
			mock.name("Mock_" + mock.getId());
		}
		if (!StringUtil.isNullOrEmpty(desc)) {
			mock.desc(desc);
		} else {
			mock.desc("");
		}
		return mock;
	}
}
