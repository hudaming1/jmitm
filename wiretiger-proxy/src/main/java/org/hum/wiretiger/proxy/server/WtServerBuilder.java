package org.hum.wiretiger.proxy.server;

import org.hum.wiretiger.proxy.config.WiretigerCoreConfig;
import org.hum.wiretiger.proxy.mock.MockHandler;

public class WtServerBuilder {
	
	private WiretigerCoreConfig config;
	

	public static WtServerBuilder init(WiretigerCoreConfig config) {
		WtServerBuilder serverBuilder = new WtServerBuilder();
		serverBuilder.config = config;
		return serverBuilder;
	}
	
	private WtServerBuilder() {
	}
	
	public WtServer build() {
		WtDefaultServer server = new WtDefaultServer(config);
		server.setMockHandler(new MockHandler(config.getMockList()));
		return server;
	}
}
