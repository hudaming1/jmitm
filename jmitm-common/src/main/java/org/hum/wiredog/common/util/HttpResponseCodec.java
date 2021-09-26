package org.hum.wiredog.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;

import org.hum.wiredog.common.constant.HttpConstant;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpResponseCodec {

	public static FullHttpResponse decode(String httpResponseWithoutBody) throws IOException {
		return decode(httpResponseWithoutBody, null);
	}
	
	public static FullHttpResponse decode(String httpResponseWithoutBody, byte[] body) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(httpResponseWithoutBody.getBytes())));
		// read request-line
		String responseLine = br.readLine();
		String[] responseLineArr = responseLine.split(" ");
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(Integer.parseInt(responseLineArr[1].trim())));
		// read header
		String headerLine = "";
		while (!"".equals(headerLine = br.readLine())) {
			if (headerLine == null) {
				break;
			}
			String key = headerLine.substring(0, headerLine.indexOf(":"));
			String value = headerLine.substring(headerLine.indexOf(":") + 1, headerLine.length());
			response.headers().set(key, value);
		}
		// read body
		if (body != null) {
			response.content().readBytes(body);
		}
		return response;
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
