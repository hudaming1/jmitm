package org.hum.wiretiger.proxy.mock;

public class CatchResponse {
	
	private ResponseInterceptor responseInterceptor;
	private ResponseRebuild responseRebuild;

	public CatchResponse eval(ResponseInterceptor responseInterceptor) {
		this.responseInterceptor = responseInterceptor;
		return this;
	}

	public CatchResponse rebuildResponse(ResponseRebuild responseRebuild) {
		this.responseRebuild = responseRebuild;
		return this;
	}

	public Mock mock() {
		return new Mock(null, responseInterceptor, null, responseRebuild);
	}

}
