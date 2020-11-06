package org.hum.wiretiger.proxy.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

public class HttpRequesCodec {

	public static FullHttpRequest decode(String httpRequestWithoutBody) throws IOException {
		return decode(httpRequestWithoutBody, null);
	}
	
	public static FullHttpRequest decode(String httpRequestWithoutBody, byte[] body) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(httpRequestWithoutBody.getBytes())));
		// read request-line
		String requestLine = br.readLine();
		String[] requestLineArr = requestLine.split(" ");
		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.valueOf(requestLineArr[2]), HttpMethod.valueOf(requestLineArr[0]), requestLineArr[1]);
		System.out.println(requestLine);
		// read header
		String headerLine = "";
		while (!"".equals(headerLine = br.readLine())) {
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
	
	public static void main(String[] args) throws IOException {
		String http = "POST /env/fetchAppListStatus HTTP/1.1\n" + 
				"Host: api.cloud.missfresh.net\n" + 
				"Proxy-Connection: keep-alive\n" + 
				"Content-Length: 96\n" + 
				"Accept: application/json, text/plain, */*\n" + 
				"accessToken: CCSTOKENMNRXGX2QINPXEZLMMVQXGZK7HIYTANJSGQ5DCNRQGQZDQMZWG43DOMZR\n" + 
				"User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36\n" + 
				"Content-Type: application/json;charset=UTF-8\n" + 
				"Origin: http://aladdin.missfresh.net\n" + 
				"Referer: http://aladdin.missfresh.net/\n" + 
				"Accept-Encoding: gzip, deflate\n" + 
				"Accept-Language: zh-CN,zh;q=0.9\n";
		String body = "{\"envId\":\"102\",\"accessToken\":\"CCSTOKENMNRXGX2QINPXEZLMMVQXGZK7HIYTANJSGQ5DCNRQGQZDQMZWG43DOMZR\"}";
		System.out.println(decode(http, body.getBytes()));
	}
}
