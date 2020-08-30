package org.hum.wiretiger.proxy.facade.lite;

import lombok.Data;

@Data
public class WiretigerSimpleSession {

	private Long pipeId;
	private Long sessionId;
	private String uri;
	private String responseCode;

}
