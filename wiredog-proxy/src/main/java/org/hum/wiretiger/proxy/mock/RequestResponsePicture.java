package org.hum.wiredog.proxy.mock;

import org.hum.wiredog.proxy.mock.netty.NettyRequestInterceptor;
import org.hum.wiredog.proxy.mock.netty.NettyRequestRebuilder;
import org.hum.wiredog.proxy.mock.netty.NettyResponseInterceptor;
import org.hum.wiredog.proxy.mock.netty.NettyResponseRebuild;

public class RequestResponsePicture {

	private NettyRequestInterceptor requestInterceptor;
	private NettyResponseInterceptor responseInterceptor;
	private NettyRequestRebuilder requestRebuilder;
	private NettyResponseRebuild responseRebuild;

	public RequestResponsePicture eval(NettyRequestInterceptor requestInterceptor, NettyResponseInterceptor responseInterceptor) {
		this.requestInterceptor = requestInterceptor;
		this.responseInterceptor = responseInterceptor;
		return this;
	}

	public RequestResponsePicture rebuildRequest(NettyRequestRebuilder requestRebuilder) {
		this.requestRebuilder = requestRebuilder;
		return this;
	}

	public RequestResponsePicture rebuildResponse(NettyResponseRebuild responseRebuild) {
		this.responseRebuild = responseRebuild;
		return this;
	}

	public Mock mock() {
		return new Mock(requestInterceptor, responseInterceptor, requestRebuilder, responseRebuild);
	}
}
