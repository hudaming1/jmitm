package org.hum.wiretiger.core.server;

import org.hum.wiretiger.config.WiretigerConfig;
import org.hum.wiretiger.core.server.wiretiger.DefaultWireTigerServer;

public class ServerFactory {

	public static WiretigerServer create(WiretigerConfig config) {
		return new DefaultWireTigerServer(config);
	}
}
