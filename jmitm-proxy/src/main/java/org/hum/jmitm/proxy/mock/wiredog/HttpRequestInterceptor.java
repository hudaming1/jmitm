package org.hum.jmitm.proxy.mock.wiredog;

public interface HttpRequestInterceptor {
	
	public boolean isHit(HttpRequest request);
}
