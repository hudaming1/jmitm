package org.hum.wiretiger.proxy.mock.wiretiger;

public interface HttpRequestInterceptor {
	
	public boolean isHit(HttpRequest request);
}
