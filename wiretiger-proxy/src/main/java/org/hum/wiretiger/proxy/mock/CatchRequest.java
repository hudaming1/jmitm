package org.hum.wiretiger.proxy.mock;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;

@Data
public class CatchRequest {
	
	private RequestInterceptor requestInterceptor;
	private RequestRebuilder requestRebuilder;
	private ResponseRebuild responseRebuild;

	public CatchRequest eval(RequestInterceptor requestInterceptor) {
		this.requestInterceptor = requestInterceptor;
		return this;
	}
	
	public CatchRequest eval(HttpRequestInterceptor requestInterceptor) {
		this.requestInterceptor = new RequestInterceptor() {
			@Override
			public boolean isHit(FullHttpRequest request) {
				return requestInterceptor.isHit(request);
			}
		};
		return this;
	}

	public CatchRequest rebuildRequest(RequestRebuilder requestRebuilder) {
		this.requestRebuilder = requestRebuilder;
		return this;
	}

	public CatchRequest rebuildResponse(ResponseRebuild responseRebuild) {
		this.responseRebuild = responseRebuild;
		return this;
	}

	public Mock mock() {
		return new Mock(requestInterceptor, null, requestRebuilder, responseRebuild);
	}
}
