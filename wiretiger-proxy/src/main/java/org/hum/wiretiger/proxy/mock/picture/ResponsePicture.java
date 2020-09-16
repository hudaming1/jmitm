package org.hum.wiretiger.proxy.mock.picture;

import org.hum.wiretiger.proxy.mock.Mock;
import org.hum.wiretiger.proxy.mock.rebuild.ResponseRebuild;

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
