package org.hum.wiredog.provider;

import org.hum.wiredog.console.common.chain.PipeManagerInvokeChain;
import org.hum.wiredog.console.common.chain.SessionManagerInvokeChain;
import org.hum.wiredog.console.http.ConsoleServer;
import org.hum.wiredog.console.http.config.wiredogConsoleConfig;
import org.hum.wiredog.console.websocket.WebSocketServer;
import org.hum.wiredog.proxy.config.wiredogCoreConfig;
import org.hum.wiredog.proxy.mock.MockHandler;
import org.hum.wiredog.proxy.server.WtDefaultServer;

import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class wiredogServerProvider {
	
	private WtDefaultServer proxyServer;
	private ConsoleServer consoleServer;
	private WebSocketServer webSocketServer;
	
	public wiredogServerProvider(wiredogCoreConfig coreConfig, wiredogConsoleConfig consoleConfig) {
		super();
		PipeManagerInvokeChain pipeManagerInvokeChain = new PipeManagerInvokeChain(null, consoleConfig.getPipeHistory());
		// proxy-server
		this.proxyServer = new WtDefaultServer(coreConfig);
		this.proxyServer.setMockHandler(new MockHandler(coreConfig.getMockList()));
		this.proxyServer.setInvokeChainInit(()-> {
			return new SessionManagerInvokeChain(pipeManagerInvokeChain, consoleConfig.getSessionHistory());
		});
		
		// console HTTP-server
		if (consoleConfig.getHttpPort() != null) {
			this.consoleServer = new ConsoleServer(consoleConfig);
		}
		
		// console WebSocket-server
		if (consoleConfig.getWebSocketPort() != null) {
			this.webSocketServer = new WebSocketServer(consoleConfig.getWebSocketPort());
		}
	}

	public void start() throws InterruptedException {
		ChannelFuture proxyStartFuture = proxyServer.start();
		if (consoleServer != null) {
			try {
				consoleServer.startJetty();
			} catch (Exception e) {
				log.error("console-server start error", e);
			}
		}
		ChannelFuture wsStartFuture = null;
		if (webSocketServer != null) {
			wsStartFuture = webSocketServer.start();
		}
		proxyStartFuture.sync();
		log.info("proxy server started, listening port:" + proxyServer.getListeningPort());
		if (wsStartFuture != null) {
			wsStartFuture.sync();
			log.info("console-ws_server started, listening port:" + webSocketServer.getPort());
		}
	}

}
