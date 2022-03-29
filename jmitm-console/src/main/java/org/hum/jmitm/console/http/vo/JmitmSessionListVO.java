package org.hum.jmitm.console.http.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class JmitmSessionListVO implements Serializable {

	private static final long serialVersionUID = 1L;

	// id
	private String sessionId;
	// 协议
	private String protocol;
	// uri
	private String uri;
	// 
	private String responseCode;
}
