package org.hum.wiretiger.console.http.config;

import lombok.Data;

@Data
public class WtConsoleConfig {

	private int httpPort;
	private int webSocketPort;
	private String webRoot;
	private String webXmlPath;
}
