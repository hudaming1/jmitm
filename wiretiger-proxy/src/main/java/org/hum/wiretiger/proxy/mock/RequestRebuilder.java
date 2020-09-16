package org.hum.wiretiger.proxy.mock;

import io.netty.handler.codec.http.HttpRequest;

public interface RequestRebuilder {

	public HttpRequest eval(HttpRequest request);
}
