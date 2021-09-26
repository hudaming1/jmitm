package org.hum.jmitm.proxy.mock.wiredog;

public interface HttpRequestRebuilder {
	
	public HttpRequest eval(HttpRequest request);
}
