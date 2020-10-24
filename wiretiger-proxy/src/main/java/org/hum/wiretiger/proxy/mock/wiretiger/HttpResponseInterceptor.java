package org.hum.wiretiger.proxy.mock.wiretiger;

public interface HttpResponseInterceptor {
	
	public boolean isHit(HttpResponse response);
}
