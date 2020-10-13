package org.hum.wiretiger.console.http.config;

import lombok.Data;

@Data
public class WiretigerConsoleConfig {

	private Integer httpPort;
	private Integer webSocketPort;
	private String webRoot;
	private String webXmlPath;
	// 默认保存"所有"连接信息
	private int pipeHistory = Integer.MAX_VALUE;
	// 默认保存"所有"请求历史快照
	private int sessionHistory = Integer.MAX_VALUE;
}
