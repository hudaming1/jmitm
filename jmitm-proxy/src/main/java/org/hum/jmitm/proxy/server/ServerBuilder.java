package org.hum.jmitm.proxy.server;

import org.hum.jmitm.proxy.config.JmitmCoreConfig;
import org.hum.jmitm.proxy.mock.MockHandler;

public class ServerBuilder {
	
	private JmitmCoreConfig config;
	

	public static ServerBuilder init(JmitmCoreConfig config) {
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
