package org.hum.wiretiger.console.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class WiretigerPipeListVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// id
	private Integer requestId;
	// 协议
	private String protocol;
	// uri
	private String name;
	//
	private String status;
}
