package org.hum.wiretiger.proxy.mock;

import org.hum.wiretiger.proxy.mock.enumtype.InterceptorType;

public class InterceptorRebuilder {
	
	private InterceptorPicture picture;

	public InterceptorRebuilder(InterceptorType type, InterceptorPicture interceptorPicture) {
		// TODO Auto-generated constructor stub
		this.picture = interceptorPicture;
	}

	public InterceptorRebuilder header(String string) {
		// TODO Auto-generated method stub
		return this;
	}

	public InterceptorRebuilder modify(HttpStringModifier modifier) {
		// TODO Auto-generated method stub
		
		return this;
	}

	public InterceptorRebuilder modify(Object string) {
		// TODO Auto-generated method stub
		return this;
	}
	

	public Mock mock() {
		return new Mock(picture, this);
	}

	public InterceptorRebuilder body() {
		// TODO Auto-generated method stub
		return this;
	}

}
