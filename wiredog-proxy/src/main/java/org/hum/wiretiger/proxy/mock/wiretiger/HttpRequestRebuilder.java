package org.hum.wiredog.proxy.mock.wiredog;

public interface HttpRequestRebuilder {
	
	public abstract HttpRequest eval(HttpRequest request);
}
