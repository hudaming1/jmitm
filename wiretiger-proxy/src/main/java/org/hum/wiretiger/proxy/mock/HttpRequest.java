package org.hum.wiretiger.proxy.mock;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpRequest {
	
	private static final String HOST = "Host";

	private FullHttpRequest fullRequest;
	
	public HttpRequest(FullHttpRequest fullRequest) {
		this.fullRequest = fullRequest;
	}
	
	public void uri(String uri) {
		fullRequest.setUri(uri);
	}
	
	public String uri() {
		return fullRequest.uri();
	}
	
	public void host(String host) {
		fullRequest.headers().set(HOST, host);
	}
	
	public String host() {
		if (fullRequest.headers() == null || fullRequest.headers().isEmpty()) {
			return null;
		}
		return fullRequest.headers().get(HOST);
	}
	
	public void header(String header, String value) {
		fullRequest.headers().set(header, value);
	}
	
	public String header(String header) {
		return fullRequest.headers().get(header);
	}
	
	public byte[] body() {
		ByteBuf requestBody = fullRequest.content();
		byte[] bytes = new byte[requestBody.readableBytes()];
		requestBody.readBytes(bytes);
		requestBody.resetReaderIndex();
		return bytes;
	}
	
	public void body(byte[] bytes) {
		ByteBuf requestBody = fullRequest.content();
		requestBody.clear().writeBytes(bytes);
		fullRequest.headers().set("Content-Length", bytes.length);
	}
	
	public FullHttpRequest toFullHttpRequest() {
		return this.fullRequest;
	}
}
