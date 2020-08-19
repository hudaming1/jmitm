package org.hum.wiretiger.console.vo;

import lombok.Data;

@Data
public class WtSessionListQueryVO {

	private String keyword;
	private Integer pipeId;
	
	public boolean isEmpty() {
		return keyword == null && pipeId == null;
	}
}
