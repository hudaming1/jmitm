package org.hum.wiretiger.console.http.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class WtPipeListQueryVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 默认仅显示存货连接
	 */
	private boolean isActive = true;

}
