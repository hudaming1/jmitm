package org.hum.wiretiger.proxy.mock.wiretiger;

public interface HttpRequestRebuilder {
	
	public abstract HttpRequest eval(HttpRequest request);
}
