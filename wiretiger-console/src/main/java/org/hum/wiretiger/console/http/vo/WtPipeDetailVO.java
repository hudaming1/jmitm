package org.hum.wiretiger.console.http.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class WtPipeDetailVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Map<String, String>> pipeEvent;
}
