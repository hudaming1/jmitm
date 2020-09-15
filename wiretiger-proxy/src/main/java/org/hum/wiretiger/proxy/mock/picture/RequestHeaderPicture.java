package org.hum.wiretiger.proxy.mock.picture;

import org.hum.wiretiger.proxy.mock.enumtype.PictureOp;

import lombok.Data;

@Data
public class RequestHeaderPicture {

	private String header;
	private PictureOp op;
	private Object value;
}
