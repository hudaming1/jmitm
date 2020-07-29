package org.hum.wiretiger;

import org.hum.wiretiger.config.WireTigerConfig;
import org.hum.wiretiger.core.server.ServerFactory;

public class ServerRun {

	public static void main(String[] args) {
		// config
		WireTigerConfig config = new WireTigerConfig();
		config.setPort(52007);
		config.setThreads(Runtime.getRuntime().availableProcessors());
		// start
		ServerFactory.create(config).start();
	}
}
