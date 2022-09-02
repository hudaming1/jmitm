package org.hum.jmitm.proxy.mock.codec;

public interface HttpRequestInterceptor {
	
	public boolean isHit(HttpRequest request);
}
