package org.hum.wiretiger.proxy.facade.lite;

import java.util.Map;

import lombok.Data;

@Data
public class WiretigerFullSession {
	
	private Long pipeId;
	
	private String method;
	private String uri;
	private String protocol;
	
	private Map<String, String> requestHeaders;
	private byte[] requestBody;
	
	private String responseCode;
	private Map<String, String> responseHeaders;
	private byte[] responseBody;
}
