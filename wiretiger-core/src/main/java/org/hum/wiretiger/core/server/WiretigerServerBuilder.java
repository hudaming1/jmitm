package org.hum.wiretiger.core.server;

import java.util.ArrayList;
import java.util.List;

import org.hum.wiretiger.config.WtCoreConfig;
import org.hum.wiretiger.core.pipe.event.EventListener;

public class WiretigerServerBuilder {
	
	private WtCoreConfig config;
	
	private List<EventListener> listeners;

	public static WiretigerServerBuilder init(WtCoreConfig config) {
		WiretigerServerBuilder serverBuilder = new WiretigerServerBuilder();
		serverBuilder.config = config;
		serverBuilder.listeners = new ArrayList<>();
		return serverBuilder;
	}
	
	private WiretigerServerBuilder() {
	}
	
	public void addEventListener(EventListener listener) {
		this.listeners.add(listener);
	}

	public WiretigerServer build() {
		DefaultWireTigerServer server = new DefaultWireTigerServer(config);
		server.setListeners(listeners);
		return server;
	}
}
