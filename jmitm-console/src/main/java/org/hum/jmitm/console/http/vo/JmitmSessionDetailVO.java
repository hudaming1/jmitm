package org.hum.jmitm.console.http.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class JmitmSessionDetailVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String requestHeader;
	private String requestBody4Source;
	private String requestBody4Parsed;
	private String responseHeader;
	private String responseBody4Source;
	private String responseBody4Parsed;
	private Long pipeId;
	private String createTime;
}
