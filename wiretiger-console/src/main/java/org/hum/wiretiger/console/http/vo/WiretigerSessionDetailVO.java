package org.hum.wiretiger.console.http.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class WiretigerSessionDetailVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String requestHeader;
	private String requestBody4Source;
	private String requestBody4Parsed;
	private String responseHeader;
	private String responseBody4Source;
	private String responseBody4Parsed;
	private Long pipeId;
}
