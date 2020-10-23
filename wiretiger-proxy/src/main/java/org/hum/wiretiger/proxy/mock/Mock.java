package org.hum.wiretiger.proxy.mock;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;

@Getter
public class Mock {
	
	private static AtomicInteger idGenerator = new AtomicInteger(1);
	private String id;
	// 定义了拦截什么样的HTTP请求
	private RequestInterceptor requestInterceptor;
	// 定义了拦截什么样的HTTP响应
	private ResponseInterceptor responseInterceptor;
	// 对拦截到的HTTP请求进行重制，你可以修改HTTP请求报文的任意信息，包括URI、Header、以及Body部分
	private RequestRebuilder requestRebuilder;
	// 对拦截到的HTTP响应进行重制，你可以修改HTTP响应报文的任意信息，包括状态码、Header、以及Body部分
	private ResponseRebuild responseRebuild;

	public Mock(RequestInterceptor requestInterceptor, ResponseInterceptor responseInterceptor, RequestRebuilder requestRebuilder, ResponseRebuild responseRebuild) {
		this.requestInterceptor = requestInterceptor;
		this.responseInterceptor = responseInterceptor;
		this.requestRebuilder = requestRebuilder;
		this.responseRebuild = responseRebuild;
		this.id = idGenerator.getAndIncrement() + "";
	}

}
