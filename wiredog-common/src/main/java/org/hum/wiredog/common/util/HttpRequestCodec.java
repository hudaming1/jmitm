package org.hum.wiredog.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;

import org.hum.wiredog.common.constant.HttpConstant;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

public class HttpRequestCodec {

	public static FullHttpRequest decode(String httpRequestWithoutBody) throws IOException {
		return decode(httpRequestWithoutBody, null);
	}
	
	public static FullHttpRequest decode(String httpRequestWithoutBody, byte[] body) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(httpRequestWithoutBody.getBytes())));
		// read request-line
		String requestLine = br.readLine();
		String[] requestLineArr = requestLine.split(" ");
		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.valueOf(requestLineArr[2]), HttpMethod.valueOf(requestLineArr[0]), requestLineArr[1]);
		// read header
		String headerLine = "";
		while (!"".equals(headerLine = br.readLine())) {
			if (headerLine == null) {
				break;
			}
			String key = headerLine.substring(0, headerLine.indexOf(":"));
			String value = headerLine.substring(headerLine.indexOf(":") + 1, headerLine.length());
			request.headers().set(key, value);
		}
		// read body
		if (body != null) {
			request.content().readBytes(body);
		}
		return request;
	}

	public static String encodeWithBody(FullHttpRequest request, String returnLine) {
		if (request == null) {
			throw new IllegalArgumentException("request mustn't be null");
		}
		returnLine = returnLine == null ? HttpConstant.RETURN_LINE : returnLine;
		StringBuilder requestStringBuilder = new StringBuilder(request.method().name() + " " + request.uri() + " " + request.protocolVersion()).append(returnLine);
		// 将Host永远放在第一个，方便查看
		requestStringBuilder.append(HttpConstant.Host + ": " + request.headers().get(HttpConstant.Host)).append(returnLine);
		for (Entry<String, String> header : request.headers()) {
			if (HttpConstant.Host.equals(header.getKey())) {
				continue;
			}
			requestStringBuilder.append(header.getKey() + ": " + header.getValue()).append(returnLine);
		}
		requestStringBuilder.append(returnLine);
		if (request.content().readableBytes() > 0) {
			byte[] bytes = new byte[request.content().readableBytes()];
			request.content().readBytes(bytes);
			requestStringBuilder.append(new String(bytes));
		}
		return requestStringBuilder.toString();
	}

	public static String encodeWithoutBody(FullHttpRequest request, String returnLine) {
		if (request == null) {
			throw new IllegalArgumentException("request mustn't be null");
		}
		returnLine = returnLine == null ? HttpConstant.RETURN_LINE : returnLine;
		StringBuilder requestStringBuilder = new StringBuilder(request.method().name() + " " + request.uri() + " " + request.protocolVersion()).append(returnLine);
		// 将Host永远放在第一个，方便查看
		requestStringBuilder.append(HttpConstant.Host + ": " + request.headers().get(HttpConstant.Host)).append(returnLine);
		for (Entry<String, String> header : request.headers()) {
			if (HttpConstant.Host.equals(header.getKey())) {
				continue;
			}
			requestStringBuilder.append(header.getKey() + ": " + header.getValue()).append(returnLine);
		}
		return requestStringBuilder.toString();
	}
}
