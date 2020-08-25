package org.hum.wiretiger.console.http.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class WtSessionListVO implements Serializable {

	private static final long serialVersionUID = 1L;

	// id
	private Long sessionId;
	// 协议
	private String protocol;
	// uri
	private String uri;
	// 
	private String responseCode;
}
