package org.hum.wiretiger.console.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class WireTigerConnectionDetailVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String requestString;
	
	private String responseString;
	
	private List<Map<String, String>> statusTimeline;
	
	private List<Map<String, String>> pipeEvent;
}
