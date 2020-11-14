package org.hum.wiredog.proxy.mock;

import java.util.concurrent.atomic.AtomicInteger;

import org.hum.wiredog.proxy.mock.netty.NettyHttpResponseMock;
import org.hum.wiredog.proxy.mock.netty.NettyRequestInterceptor;
import org.hum.wiredog.proxy.mock.netty.NettyRequestRebuilder;
import org.hum.wiredog.proxy.mock.netty.NettyResponseInterceptor;
import org.hum.wiredog.proxy.mock.netty.NettyResponseRebuild;

import lombok.Getter;

@Getter
public class Mock {
	
	private static AtomicInteger idGenerator = new AtomicInteger(1);
	private String id;
	private String name;
	private String desc;
	private MockStatus status;
	// 定义了拦截什么样的HTTP请求
	private NettyRequestInterceptor requestInterceptor;
	// 定义了拦截什么样的HTTP响应
	private NettyResponseInterceptor responseInterceptor;
	// 对拦截到的HTTP请求进行重制，你可以修改HTTP请求报文的任意信息，包括URI、Header、以及Body部分
	private NettyRequestRebuilder requestRebuilder;
	// 对拦截到的HTTP响应进行重制，你可以修改HTTP响应报文的任意信息，包括状态码、Header、以及Body部分
	private NettyResponseRebuild responseRebuild;
	// 对拦截到的HTTP请求进行Mock
	private NettyHttpResponseMock httpMockResponse;

	public Mock(NettyRequestInterceptor requestInterceptor, NettyResponseInterceptor responseInterceptor, NettyRequestRebuilder requestRebuilder, NettyResponseRebuild responseRebuild, NettyHttpResponseMock httpMockResponse) {
		this.requestInterceptor = requestInterceptor;
		this.responseInterceptor = responseInterceptor;
		this.requestRebuilder = requestRebuilder;
		this.responseRebuild = responseRebuild;
		this.httpMockResponse = httpMockResponse;
		this.id = idGenerator.getAndIncrement() + "";
		this.status = MockStatus.Enabled;
	}
	
	public Mock name(String name) {
		this.name = name;
		return this;
	}
	
	public Mock desc(String desc) {
		this.desc = desc;
		return this;
	}
	
	public Mock status(MockStatus status) {
		this.status = status;
		return this;
	}
	
	public String name() {
		return this.name;
	}
	
	public String desc() {
		return this.desc;
	}
	
	public String id() {
		return this.id;
	}
	
	public MockStatus status() {
		return status;
	}
}
