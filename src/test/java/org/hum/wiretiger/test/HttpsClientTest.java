package org.hum.wiretiger.test;

import org.hum.wiretiger.core.handler.helper.HttpsClient;
import org.junit.Test;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

public class HttpsClientTest {

	@Test
	public void test() throws Exception {
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/v1/buckets/main/collections/ms-language-packs/records/cfr-v1-zh-CN");
		FullHttpResponse resp = HttpsClient.send("firefox.settings.services.mozilla.com", 443, httpRequest);
		System.out.println("read resp=");
		System.out.println(resp);
	}

	@Test
	public void test2() throws Exception {
		HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
		FullHttpResponse resp = HttpsClient.send("www.baidu.com", 443, httpRequest);
		System.out.println("read resp=");
		System.out.println(resp);
	}
}
