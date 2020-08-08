package org.hum.wiretiger.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

public class MyTest {

	@Test
	public void uriTest() throws Exception {
		URI uri = new URI("https://www.qiandu.com:8080/goods/index.html?username=dgh&passwd=123#j2se");
		System.out.println("scheme             : " + uri.getScheme());
		System.out.println("SchemeSpecificPart : " + uri.getSchemeSpecificPart());
		System.out.println("Authority          : " + uri.getAuthority());
		System.out.println("host               : " + uri.getHost());
		System.out.println("port               : " + uri.getPort());
		System.out.println("path               : " + uri.getPath());
		System.out.println("query              : " + uri.getQuery());
		System.out.println("fragment           : " + uri.getFragment());
		System.out.println("rawpath            : " + uri.getRawPath());
	}

	// ip.src_host==54.230.175.67||ip.dst_host==54.230.175.67
	@Test
	public void test3() throws UnknownHostException, IOException {
//		String hexStreamString = "16030300f5010000f103035f2e1cfc3316dc8b626126bfc8d9e81f37fb7ec79d00b1cc6146bdd3ef2aba3a000056c024c028003dc026c02a006b006ac00ac0140035c005c00f00390038c023c027003cc025c02900670040c009c013002fc004c00e00330032c02cc02bc030009dc02ec032009f00a3c02f009cc02dc031009e00a200ff01000072000a001600140017001800190009000a000b000c000d000e0016000b00020100000d001c001a0603060105030501040304010402030303010302020302010202001700000000002a002800002566697265666f782e73657474696e67732e73657276696365732e6d6f7a696c6c612e636f6d";
		String hexStreamString = "16030300cb010000c703035f2e1cfc3316dc8b626126bfc8d9e81f37fb7ec79d00b1cc6146bdd3ef2aba3a000056c024c028003dc026c02a006b006ac00ac0140035c005c00f00390038c023c027003cc025c02900670040c009c013002fc004c00e00330032c02cc02bc030009dc02ec032009f00a3c02f009cc02dc031009e00a200ff01000048000a001600140017001800190009000a000b000c000d000e0016000b00020100000d001c001a060306010503050104030401040203030301030202030201020200170000";
		byte[] bytes = DatatypeConverter.parseHexBinary(hexStreamString);
		Socket socket = new Socket("54.230.175.67", 443);
		socket.getOutputStream().write(bytes);
		socket.getOutputStream().flush();
		System.out.println("write=" + Arrays.toString(bytes));
		InputStream inputStream = socket.getInputStream();
		byte[] resp = new byte[1024];
		inputStream.read(resp);
		System.out.println(Arrays.toString(resp));
	}
	
	/**
	 * 实例证明：使用netty连接firefox.settings.services.mozilla.com的clientHello失败原因，就是因为没有extension.server_name
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	@Test
	public void test5() throws UnknownHostException, IOException {
//		String hexStreamString = "16030300860100008203035f2e1cb8b70645dc0a5fa33cb24fb5c2d31515a1e7fa6987af0949391bb080c3000010c02cc02bc02fc013c014009c002f003501000049000a001600140017001800190009000a000b000c000d000e0016000b00020100000d001c001a060306010503050104030401040203030301030202030201020200170000ff01000100";
		String hexStreamString = "16030300b4010000b003035f2e1cb8b70645dc0a5fa33cb24fb5c2d31515a1e7fa6987af0949391bb080c3000010c02cc02bc02fc013c014009c002f003501000077000a001600140017001800190009000a000b000c000d000e0016000b00020100000d001c001a060306010503050104030401040203030301030202030201020200170000ff01000100";
		hexStreamString += "0000002a002800002566697265666f782e73657474696e67732e73657276696365732e6d6f7a696c6c612e636f6d";
		byte[] bytes = DatatypeConverter.parseHexBinary(hexStreamString);
		Socket socket = new Socket("54.230.175.67", 443);
		socket.getOutputStream().write(bytes);
		socket.getOutputStream().flush();
		System.out.println("write=" + Arrays.toString(bytes));
		InputStream inputStream = socket.getInputStream();
		byte[] resp = new byte[1024];
		inputStream.read(resp);
		System.out.println(Arrays.toString(resp));
	}
	
	
	
	@Test
	public void test4() throws UnknownHostException, IOException {
		byte[] bytes = new byte[] { 22, 3, 3, 0, -11, 1, 0, 0, -15, 3, 3, 95, 46, 28, -4, 51, 22, -36, -117, 98, 97, 38, -65, -56, -39, -24, 31, 55, -5, 126, -57, -99, 0, -79, -52, 97, 70, -67, -45, -17, 42, -70, 58, 0, 0, 86, -64, 36, -64, 40, 0, 61, -64, 38, -64, 42, 0, 107, 0, 106, -64, 10, -64, 20, 0, 53, -64, 5, -64, 15, 0, 57, 0, 56, -64, 35, -64, 39, 0, 60, -64, 37, -64, 41, 0, 103, 0, 64, -64, 9, -64, 19, 0, 47, -64, 4, -64, 14, 0, 51, 0, 50, -64, 44, -64, 43, -64, 48, 0, -99, -64, 46, -64, 50, 0, -97, 0, -93, -64, 47, 0, -100, -64, 45, -64, 49, 0, -98, 0, -94, 0, -1, 1, 0, 0, 114, 0, 10, 0, 22, 0, 20, 0, 23, 0, 24, 0, 25, 0, 9, 0, 10, 0, 11, 0, 12, 0, 13, 0, 14, 0, 22, 0, 11, 0, 2, 1, 0, 0, 13, 0, 28, 0, 26, 6, 3, 6, 1, 5, 3, 5, 1, 4, 3, 4, 1, 4, 2, 3, 3, 3, 1, 3, 2, 2, 3, 2, 1, 2, 2, 0, 23, 0, 0, 0, 0, 0, 42, 0, 40, 0, 0, 37, 102, 105, 114, 101, 102, 111, 120, 46, 115, 101, 116, 116, 105, 110, 103, 115, 46, 115, 101, 114, 118, 105, 99, 101, 115, 46, 109, 111, 122, 105, 108, 108, 97, 46, 99, 111, 109 };
		Socket socket = new Socket("54.230.175.67", 443);
		socket.getOutputStream().write(bytes);
		socket.getOutputStream().flush();
		InputStream inputStream = socket.getInputStream();
		byte[] resp = new byte[1024];
		inputStream.read(resp);
		System.out.println(Arrays.toString(resp));
	}

	@Test
	public void test2() throws IOException {
		SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory();
		String[] cipherSuites = factory.getSupportedCipherSuites();
		SSLSocket sslSocket = (SSLSocket) factory.createSocket();

		// Choose the exact ciphers you need from the available
		String[] filteredCipherSuites = cipherSuites;

		System.out.println(Arrays.toString(filteredCipherSuites));
		// sslSocket.setEnabledCipherSuites(filteredCipherSuites);

		sslSocket.connect(new InetSocketAddress("firefox.settings.services.mozilla.com", 443));
		sslSocket.getHandshakeSession();
	}

}
