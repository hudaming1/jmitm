package org.hum.wiretiger.config;

import lombok.Data;

@Data
public class WiretigerConfig {

	// 监听端口
	private Integer port;
	// 线程池
	private Integer threads;
	// 控制台端口
	private Integer consolePort;
	// websocket端口
	private Integer wsPort;
	//
	private boolean isDebug;
}
