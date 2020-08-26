package org.hum.wiretiger.console.http.config;

import lombok.Data;

@Data
public class WtConsoleHttpConfig {

	private int port;
	private String webRoot;
	private String webXmlPath;
}
