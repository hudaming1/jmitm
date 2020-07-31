package org.hum.wiretiger.config;

import lombok.Data;

@Data
public class WireTigerConfig {

	// 监听端口
	private Integer port;
	// 线程池
	private Integer threads;
	// 控制台端口
	private Integer consolePort;
}
