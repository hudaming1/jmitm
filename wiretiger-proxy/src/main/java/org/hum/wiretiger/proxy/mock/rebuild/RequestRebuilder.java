package org.hum.wiretiger.proxy.mock.rebuild;

import lombok.Data;

@Data
public class RequestRebuilder {

	private RequestUriRebuilder uriRebuilder;
	private RequestHeaderRebuilder headerRebuilder;
	private RequestBodyRebuilder bodyRebuilder;
}
