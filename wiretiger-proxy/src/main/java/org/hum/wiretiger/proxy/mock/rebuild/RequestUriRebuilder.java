package org.hum.wiretiger.proxy.mock.rebuild;

import org.hum.wiretiger.proxy.mock.enumtype.RebuildOp;

import lombok.Data;

@Data
public class RequestUriRebuilder {

	private RebuildOp op;
	private Object value;
}
