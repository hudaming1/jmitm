package org.hum.wiretiger;

import org.hum.wiretiger.config.WiretigerConfig;
import org.hum.wiretiger.core.server.ServerFactory;

public class WiretigerServerRun {

	public static void main(String[] args) {
		// config
		WiretigerConfig config = new WiretigerConfig();
		config.setPort(52007);
		config.setConsolePort(8080);
		config.setThreads(Runtime.getRuntime().availableProcessors());
		config.setDebug(false);
		// start
		ServerFactory.create(config).start();
	}
}
