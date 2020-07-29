package org.hum.wiretiger.test;

import org.hum.wiretiger.core.handler.HttpsClient;
import org.junit.Test;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

public class HttpsClientTest {

	@Test
	public void test() throws Exception {
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
		FullHttpResponse resp = HttpsClient.send("www.baidu.com", 443, httpRequest);
		System.out.println("read resp=");
		System.out.println(resp);
	}
}
