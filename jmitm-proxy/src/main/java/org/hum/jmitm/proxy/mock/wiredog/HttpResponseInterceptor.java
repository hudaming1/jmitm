package org.hum.jmitm.proxy.mock.wiredog;

public interface HttpResponseInterceptor {
	
	public boolean isHit(HttpResponse response);
}
