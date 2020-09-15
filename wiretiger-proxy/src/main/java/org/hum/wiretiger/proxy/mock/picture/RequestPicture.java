package org.hum.wiretiger.proxy.mock.picture;

import java.util.List;

import lombok.Data;

@Data
public class RequestPicture {

	private RequestUriPicture uriPicture;
	private List<RequestHeaderPicture> headerPicture;
	private Object bodyRegx;
}
