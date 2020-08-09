package org.hum.wiretiger.core.server;

import org.hum.wiretiger.config.WireTigerConfig;
import org.hum.wiretiger.core.server.wiretiger.DefaultWireTigerServer;

public class ServerFactory {

	public static WireTigerServer create(WireTigerConfig config) {
		return new DefaultWireTigerServer(config);
	}
}
