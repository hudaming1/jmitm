package org.hum.wiretiger.test;

import org.junit.Test;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

public class HttpsClientTest {

	/**
	 * VM: -Djavax.net.debug=ssl,handshake
	 * jdk8在handshake时，没有输出ext.server_name导致个别tls服务器无法识别
	 * https://bugs.java.com/bugdatabase/view_bug.do?xd_co_f=95332980-b9de-444f-844a-aae1726d210b&bug_id=JDK-8144566
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		// 访问火狐会握手失败，打印NettyLogging日志发现，clientSayHello后，Server返回alert，在这个阶段失败，一般原因都是服务端无法找到匹配的SSL/TLS版本或CipherSuits
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
