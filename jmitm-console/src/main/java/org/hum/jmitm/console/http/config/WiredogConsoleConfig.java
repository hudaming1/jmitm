package org.hum.jmitm.console.http.config;

import lombok.Data;

@Data
public class WiredogConsoleConfig {

	private Integer httpPort;
	private Integer webSocketPort;
	private String webRoot;
	private String webXmlPath;
	// 是否显示控制台请求
	private boolean isShowConsoleSession;
	// 默认保存"所有"连接信息
	private int pipeHistory = Integer.MAX_VALUE;
	// 默认保存"所有"请求历史快照
	private int sessionHistory = Integer.MAX_VALUE;
}
