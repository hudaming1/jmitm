package org.hum.wiretiger.proxy.mock.picture;

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

	public InterceptorPicture evalRequest(HttpRequestInterceptor interceptor) {
		// TODO Auto-generated method stub
		
		return this;
	}

	public InterceptorPicture evalResponse(HttpResponseInterceptor httpResponseInterceptor) {
		// TODO Auto-generated method stub
		return this;
	}

	public InterceptorRebuilder rebuild(InterceptorType request) {
		// TODO Auto-generated method stub
		return new InterceptorRebuilder(request, this);
	}
}
