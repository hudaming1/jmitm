package org.hum.jmitm.proxy.mock.codec;

public interface HttpRequestRebuilder {
	
	public HttpRequest eval(HttpRequest request);
}
