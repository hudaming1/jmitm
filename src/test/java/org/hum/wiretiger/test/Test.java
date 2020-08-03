package org.hum.wiretiger.test;

import java.net.URI;

public class Test {
	
	@org.junit.Test
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
}
