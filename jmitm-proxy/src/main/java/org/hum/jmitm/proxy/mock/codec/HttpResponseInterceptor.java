package org.hum.jmitm.proxy.mock.codec;

public interface HttpResponseInterceptor {
	
	public boolean isHit(HttpResponse response);
}
