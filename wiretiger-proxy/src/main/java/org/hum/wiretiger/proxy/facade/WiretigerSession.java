package org.hum.wiretiger.proxy.facade;

import lombok.Data;

@Data
public class WiretigerSession {

	private String sessionId;
	private String uri;
	private String respStatus;
	
}
