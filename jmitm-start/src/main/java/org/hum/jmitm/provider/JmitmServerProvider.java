package org.hum.jmitm.provider;

import org.hum.jmitm.console.common.chain.PipeManagerInvokeChain;
import org.hum.jmitm.console.common.chain.SessionManagerInvokeChain;
import org.hum.jmitm.console.http.ConsoleServer;
import org.hum.jmitm.console.http.config.JmitmConsoleConfig;
import org.hum.jmitm.console.websocket.WebSocketServer;
import org.hum.jmitm.proxy.config.JmitmCoreConfig;
import org.hum.jmitm.proxy.mock.MockHandler;
import org.hum.jmitm.proxy.server.DefaultServer;

import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JmitmServerProvider {
	
	private DefaultServer proxyServer;
	private ConsoleServer consoleServer;
	private WebSocketServer webSocketServer;
	
	public JmitmServerProvider(JmitmCoreConfig coreConfig, JmitmConsoleConfig consoleConfig) {
		PipeManagerInvokeChain pipeManagerInvokeChain = new PipeManagerInvokeChain(null, consoleConfig.getPipeHistory());
		// proxy-server
		this.proxyServer = new DefaultServer(coreConfig);
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
