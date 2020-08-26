package org.hum.wiretiger;

import org.hum.wiretiger.config.WtCoreConfig;
import org.hum.wiretiger.proxy.server.ServerFactory;

public class WiretigerServerRun {

	public static void main(String[] args) {
		// config
		WtCoreConfig config = new WtCoreConfig();
		config.setPort(52007);
		config.setWsPort(52996);
		config.setConsolePort(8080);
		config.setThreads(Runtime.getRuntime().availableProcessors());
		config.setDebug(false);
		// start
		ServerFactory.create(config).start();
	}
}
