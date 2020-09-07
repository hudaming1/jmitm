package org.hum.wiretiger.console.http.vo;

import lombok.Data;

@Data
public class WiretigerSessionListQueryVO {

	private String keyword;
	private Long pipeId;
	
	public boolean isEmpty() {
		return (keyword == null || keyword.isEmpty()) && pipeId == null;
	}
}
