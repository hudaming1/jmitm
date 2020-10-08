package org.hum.wiretiger.proxy.mock;

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
