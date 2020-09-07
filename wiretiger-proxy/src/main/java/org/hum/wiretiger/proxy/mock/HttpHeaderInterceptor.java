package org.hum.wiretiger.proxy.mock;

public interface HttpHeaderInterceptor {

	public boolean eval(String headerValue);
}
