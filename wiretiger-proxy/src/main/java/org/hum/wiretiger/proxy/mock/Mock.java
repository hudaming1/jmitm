package org.hum.wiretiger.proxy.mock;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;

@Getter
public class Mock {
	
	private static AtomicInteger idGenerator = new AtomicInteger(1);
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
		this.id = idGenerator.getAndIncrement() + "";
	}

}
