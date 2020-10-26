package org.hum.wiretiger.proxy.mock.wiretiger;

import org.hum.wiretiger.common.constant.HttpConstant;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpResponse {

	private FullHttpResponse fullResponse;
	
	public HttpResponse(FullHttpResponse fullResponse) {
		this.fullResponse = fullResponse;
	}
	
	public HttpResponse code(int code) {
		this.fullResponse.setStatus(HttpResponseStatus.valueOf(code));
		return this;
	}

	public HttpResponseStatus code() {
		return this.fullResponse.status();
	}
	
	public HttpResponse header(String header, String value) {
		this.fullResponse.headers().set(header, value);
		return this;
	}
	
	public String header(String header) {
		return this.fullResponse.headers().get(header);
	}

	public byte[] body() {
		ByteBuf requestBody = fullResponse.content().duplicate();
		byte[] bytes = new byte[requestBody.readableBytes()];
		requestBody.readBytes(bytes);
		return bytes;
	}
	
	public HttpResponse body(byte[] bytes) {
		ByteBuf requestBody = fullResponse.content();
		requestBody.clear().writeBytes(bytes);
		// 修改body，自动帮忙计算content-length
		fullResponse.headers().set(HttpConstant.ContentLength, bytes.length);
		return this;
	}

	public HttpResponse removeHeader(String header) {
		this.fullResponse.headers().remove(header);
		return this;
	}
	
	public FullHttpResponse toFullHttpResponse() {
		return this.fullResponse;
	}

}
