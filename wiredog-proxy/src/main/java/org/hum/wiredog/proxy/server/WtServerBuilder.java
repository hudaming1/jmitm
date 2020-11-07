package org.hum.wiredog.proxy.server;

import org.hum.wiredog.proxy.config.WiredogCoreConfig;
import org.hum.wiredog.proxy.mock.MockHandler;

public class WtServerBuilder {
	
	private WiredogCoreConfig config;
	

	public static WtServerBuilder init(WiredogCoreConfig config) {
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
