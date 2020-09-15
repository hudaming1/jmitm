package org.hum.wiretiger.proxy.mock.picture;

import org.hum.wiretiger.proxy.mock.enumtype.PictureOp;

import lombok.Data;

@Data
public class RequestUriPicture {

	private PictureOp op;
	private String value;
}
