package org.hum.wiredog.proxy.mock.wiredog;

public interface HttpResponseInterceptor {
	
	public boolean isHit(HttpResponse response);
}
