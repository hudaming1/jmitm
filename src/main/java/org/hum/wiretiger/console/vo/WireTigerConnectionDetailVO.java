package org.hum.wiretiger.console.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class WireTigerConnectionDetailVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String requestString;
	
	private String responseString;
}
