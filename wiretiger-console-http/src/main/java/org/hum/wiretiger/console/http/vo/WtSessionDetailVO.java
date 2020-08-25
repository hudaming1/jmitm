package org.hum.wiretiger.console.http.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class WtSessionDetailVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String request;
	private String responseHeader;
	private String responseBody4Source;
	private String responseBody4Parsed;
}
