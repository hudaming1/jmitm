package org.hum.wiretiger.proxy.mock.wiretiger;

import org.hum.wiretiger.common.constant.HttpConstant;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

public class HttpRequest {

	private FullHttpRequest fullRequest;
	
	public HttpRequest(FullHttpRequest fullRequest) {
		this.fullRequest = fullRequest;
	}
	
	public HttpRequest uri(String uri) {
		fullRequest.setUri(uri);
		return this;
	}
	
	public String uri() {
		return fullRequest.uri();
	}
	
	public HttpRequest host(String host) {
		fullRequest.headers().set(HttpConstant.Host, host);
		return this;
	}
	
	public String host() {
		if (fullRequest.headers() == null || fullRequest.headers().isEmpty()) {
			return null;
		}
		return fullRequest.headers().get(HttpConstant.Host);
	}
	
	public HttpRequest header(String header, String value) {
		fullRequest.headers().set(header, value);
		return this;
	}
	
	public String header(String header) {
		return fullRequest.headers().get(header);
	}
	
	public byte[] body() {
		ByteBuf requestBody = fullRequest.content().duplicate();
		byte[] bytes = new byte[requestBody.readableBytes()];
		requestBody.readBytes(bytes);
		return bytes;
	}
	
	public HttpRequest body(byte[] bytes) {
		ByteBuf requestBody = fullRequest.content();
		requestBody.clear().writeBytes(bytes);
		// 修改body，自动帮忙计算content-length
		fullRequest.headers().set(HttpConstant.ContentLength, bytes.length);
		return this;
	}

	public HttpMethod method() {
		return this.fullRequest.method();
	}
	
	public HttpRequest method(HttpMethod method) {
		this.fullRequest.setMethod(method);
		return this;
	}
	
	public FullHttpRequest toFullHttpRequest() {
		return this.fullRequest;
	}
}
