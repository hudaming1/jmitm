package org.hum.jmitm.proxy.server;

import org.hum.jmitm.proxy.config.WiredogCoreConfig;
import org.hum.jmitm.proxy.mock.MockHandler;

public class ServerBuilder {
	
	private WiredogCoreConfig config;
	

	public static ServerBuilder init(WiredogCoreConfig config) {
		ServerBuilder serverBuilder = new ServerBuilder();
		serverBuilder.config = config;
		return serverBuilder;
	}
	
	private ServerBuilder() {
	}
	
	public Server build() {
		DefaultServer server = new DefaultServer(config);
		server.setMockHandler(new MockHandler(config.getMockList()));
		return server;
	}
}
