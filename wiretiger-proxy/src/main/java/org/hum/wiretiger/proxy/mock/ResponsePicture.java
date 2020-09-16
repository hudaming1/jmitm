package org.hum.wiretiger.proxy.mock;

public class ResponsePicture {
	
	private ResponseInterceptor responseInterceptor;
	private ResponseRebuild responseRebuild;

	public ResponsePicture eval(ResponseInterceptor responseInterceptor) {
		this.responseInterceptor = responseInterceptor;
		return this;
	}

	public ResponsePicture rebuildResponse(ResponseRebuild responseRebuild) {
		this.responseRebuild = responseRebuild;
		return this;
	}

	public Mock mock() {
		return new Mock(null, responseInterceptor, null, responseRebuild);
	}

}
