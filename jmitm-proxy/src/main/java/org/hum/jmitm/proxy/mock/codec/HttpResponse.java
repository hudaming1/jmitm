package org.hum.jmitm.proxy.mock.codec;

import org.hum.jmitm.common.constant.HttpConstant;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpResponse {

	private FullHttpResponse fullResponse;
	
	public HttpResponse() {
		fullResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
	}
	
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
	
	public HttpResponse header(String header, Object value) {
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
	
	public HttpResponse bodyClear() {
		fullResponse.content().clear();
		fullResponse.headers().set(HttpConstant.ContentLength, 0);
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
