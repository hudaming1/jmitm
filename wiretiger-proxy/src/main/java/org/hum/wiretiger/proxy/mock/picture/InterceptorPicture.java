package org.hum.wiretiger.proxy.mock.picture;

import org.hum.wiretiger.proxy.mock.HttpHeaderInterceptor;
import org.hum.wiretiger.proxy.mock.HttpRequestInterceptor;
import org.hum.wiretiger.proxy.mock.HttpResponseInterceptor;
import org.hum.wiretiger.proxy.mock.InterceptorRebuilder;
import org.hum.wiretiger.proxy.mock.enumtype.InterceptorType;

import lombok.Getter;

@Getter
public class InterceptorPicture {
	
	private RequestPicture requestPicture;
	private Object responsePicture;
	private Object requestRebuilder;
	private Object responseRebuilder;
	
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
