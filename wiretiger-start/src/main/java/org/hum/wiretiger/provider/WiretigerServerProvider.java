package org.hum.wiretiger.provider;

import java.util.List;

import org.hum.wiretiger.console.http.ConsoleServer;
import org.hum.wiretiger.console.http.config.WiretigerConsoleConfig;
import org.hum.wiretiger.console.websocket.WebSocketServer;
import org.hum.wiretiger.proxy.config.WiretigerCoreConfig;
import org.hum.wiretiger.proxy.facade.event.EventListener;
import org.hum.wiretiger.proxy.mock.MockHandler;
import org.hum.wiretiger.proxy.server.WtDefaultServer;

public class WiretigerServerProvider {
	
	private WtDefaultServer proxyServer;
	private ConsoleServer consoleServer;
	private WebSocketServer webSocketServer;
	
	public WiretigerServerProvider(WiretigerCoreConfig coreConfig, WiretigerConsoleConfig consoleConfig, List<EventListener> listeners) {
		super();
		// proxy-server
		this.proxyServer = new WtDefaultServer(coreConfig);
		this.proxyServer.setListeners(listeners);
		this.proxyServer.setMockHandler(new MockHandler(coreConfig.getMockList()));
		
		// console HTTP-server
		this.consoleServer = new ConsoleServer(consoleConfig);
		
		// console WebSocket-server
		this.webSocketServer = new WebSocketServer(consoleConfig.getWebSocketPort());
	}

	public void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				proxyServer.start();				
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					consoleServer.startJetty();
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				webSocketServer.start();			
			}
		}).start();
		
	}

}
