package org.hum.wiredog.console.http.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class WiredogPipeListVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// id
	private String pipeId;
	// 协议
	private String protocol;
	// uri
	private String name;
	//
	private String status;
}
