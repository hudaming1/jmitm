package org.hum.wiretiger.core.server;

import org.hum.wiretiger.config.WiretigerConfig;

public class ServerFactory {

	public static WiretigerServer create(WiretigerConfig config) {
		return new DefaultWireTigerServer(config);
	}
}
