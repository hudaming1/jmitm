package org.hum.wiretiger.proxy.config;

import lombok.Data;

@Data
public class WtCoreConfig {

	// 监听端口
	private Integer port;
	// 线程池
	private Integer threads;
	//
	private boolean isDebug;
}
