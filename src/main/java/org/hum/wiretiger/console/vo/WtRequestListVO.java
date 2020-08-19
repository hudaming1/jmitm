package org.hum.wiretiger.console.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class WtRequestListVO implements Serializable {

	private static final long serialVersionUID = 1L;

	// id
	private Long requestId;
	// 协议
	private String protocol;
	// uri
	private String uri;
	// 
	private String responseCode;
}
