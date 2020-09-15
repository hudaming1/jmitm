package org.hum.wiretiger.proxy.mock;

import org.hum.wiretiger.proxy.mock.picture.InterceptorPicture;

import lombok.Getter;

@Getter
public class Mock {
	
	private String id;
	private InterceptorPicture picture;
	private InterceptorRebuilder interceptorRebuilder;

	public Mock(String id, InterceptorPicture picture, InterceptorRebuilder interceptorRebuilder) {
		this.id = id;
		this.picture = picture;
		this.interceptorRebuilder = interceptorRebuilder;
	}

	
}
