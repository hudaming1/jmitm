package org.hum.wiretiger.proxy.mock;

import org.hum.wiretiger.proxy.mock.enumtype.InterceptorType;
import org.hum.wiretiger.proxy.mock.picture.InterceptorPicture;
import org.hum.wiretiger.proxy.mock.rebuild.HttpResponseRebuild;
import org.hum.wiretiger.proxy.mock.rebuild.RequestRebuilder;

import lombok.Getter;

@Getter
public class InterceptorRebuilder {
	
	private InterceptorPicture picture;
	private RequestRebuilder requestRebuilder;

	public InterceptorRebuilder(InterceptorType type, InterceptorPicture interceptorPicture) {
		// TODO Auto-generated constructor stub
		this.picture = interceptorPicture;
	}

	public Mock mock() {
		return new Mock(System.currentTimeMillis() + "", picture, this);
	}

	public InterceptorRebuilder eval(HttpResponseRebuild httpResponseRebuild) {
		
		return this;
	}
}
