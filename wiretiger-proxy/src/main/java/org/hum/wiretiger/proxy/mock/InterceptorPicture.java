package org.hum.wiretiger.proxy.mock;

import org.hum.wiretiger.proxy.mock.enumtype.InterceptorType;

import io.netty.handler.codec.http.HttpRequest;

public class InterceptorPicture {

	public InterceptorPicture(InterceptorType request) {
		// TODO Auto-generated constructor stub
	}

	public InterceptorPicture eval(HttpRequestInterceptor interceptor) {
		// TODO Auto-generated method stub
		
		return this;
	}

	public InterceptorPicture eval(HttpHeaderInterceptor interceptor) {
		// TODO Auto-generated method stub
		
		return this;
	}

	public InterceptorPicture eval(HttpResponseInterceptor httpResponseInterceptor) {
		// TODO Auto-generated method stub
		return this;
	}

	public InterceptorPicture header(String string) {
		// TODO Auto-generated method stub
		return this;
	}

	public InterceptorPicture equal(String string) {
		// TODO Auto-generated method stub
		
		return this;
	}

	public InterceptorPicture like(String string) {
		// TODO Auto-generated method stub
	
		return this;
	}

	public InterceptorPicture uri(String string) {
		// TODO Auto-generated method stub
		
		return this;
	}

	public InterceptorPicture keyword(String string) {
		// TODO Auto-generated method stub
		
		return this;
	}

	public InterceptorRebuilder rebuild(InterceptorType request) {
		// TODO Auto-generated method stub
		return new InterceptorRebuilder(request, this);
	}

}
