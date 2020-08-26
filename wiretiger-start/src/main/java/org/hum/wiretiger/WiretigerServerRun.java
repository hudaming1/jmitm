package org.hum.wiretiger;

import org.hum.wiretiger.proxy.config.WtCoreConfig;
import org.hum.wiretiger.proxy.server.WtServerBuilder;

public class WiretigerServerRun {

	public static void main(String[] args) {
		// config
		WtCoreConfig config = new WtCoreConfig();
		config.setPort(52007);
		config.setThreads(Runtime.getRuntime().availableProcessors());
		config.setDebug(false);
		
		WtServerBuilder.init(config).build().start();
	}
}
