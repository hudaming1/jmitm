package org.hum.wiretiger.proxy.mock;

import org.hum.wiretiger.proxy.mock.picture.RequestInterceptor;
import org.hum.wiretiger.proxy.mock.picture.ResponseInterceptor;
import org.hum.wiretiger.proxy.mock.rebuild.RequestRebuilder;
import org.hum.wiretiger.proxy.mock.rebuild.ResponseRebuild;

import lombok.Getter;

@Getter
public class Mock {
	
	private String id;
	private RequestInterceptor requestInterceptor;
	private ResponseInterceptor responseInterceptor;
	private RequestRebuilder requestRebuilder;
	private ResponseRebuild responseRebuild;

	public Mock(RequestInterceptor requestInterceptor, ResponseInterceptor responseInterceptor, RequestRebuilder requestRebuilder, ResponseRebuild responseRebuild) {
		this.requestInterceptor = requestInterceptor;
		this.responseInterceptor = responseInterceptor;
		this.requestRebuilder = requestRebuilder;
		this.responseRebuild = responseRebuild;
		this.id = System.nanoTime() + "";
	}

}
