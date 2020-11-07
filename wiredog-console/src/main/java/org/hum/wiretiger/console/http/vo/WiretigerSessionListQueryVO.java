package org.hum.wiredog.console.http.vo;

import lombok.Data;

@Data
public class wiredogSessionListQueryVO {

	private String keyword;
	private String host;
	private Long pipeId;
	
	public boolean isEmpty() {
		return (keyword == null || keyword.isEmpty()) 
				&& pipeId == null 
				&& (host == null || host.isEmpty());
	}
}
