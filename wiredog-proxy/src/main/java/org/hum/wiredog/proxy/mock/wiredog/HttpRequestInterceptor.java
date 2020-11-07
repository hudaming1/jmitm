package org.hum.wiredog.proxy.mock.wiredog;

public interface HttpRequestInterceptor {
	
	public boolean isHit(HttpRequest request);
}
