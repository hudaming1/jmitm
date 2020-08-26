package org.hum.wiretiger.proxy.facade.event;

import lombok.Data;

@Data
public class WiretigerSession {

	private String sessionId;
	private String uri;
	private String respStatus;
	
}
