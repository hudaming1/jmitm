package org.hum.wiretiger.provider;

import java.util.ArrayList;
import java.util.List;

import org.hum.wiretiger.console.http.config.WiretigerConsoleConfig;
import org.hum.wiretiger.proxy.config.WiretigerCoreConfig;
import org.hum.wiretiger.proxy.facade.event.EventListener;
import org.hum.wiretiger.proxy.mock.Mock;

public class WiretigerBuilder {

	private WiretigerCoreConfig coreConfig = new WiretigerCoreConfig();
	private WiretigerConsoleConfig consoleConfig = new WiretigerConsoleConfig();
	private List<EventListener> listeners = new ArrayList<>();
	
	public void addMock(Mock... mocks) {
		if (mocks == null || mocks.length == 0) {
			return ;
		}
		for (Mock mock : mocks) {
			this.coreConfig.addMock(mock);
		}
	}

	public int proxyPort() {
		return coreConfig.getPort();
	}

	public void proxyPort(int port) {
		coreConfig.setPort(port);
	}
	
	public int threads() {
		return coreConfig.getThreads();
	}

	public void threads(int threads) {
		coreConfig.setThreads(threads);
	}

	public boolean debug() {
		return coreConfig.isDebug();
	}

	public void debug(boolean isDebug) {
		coreConfig.setDebug(isDebug);
	}
	
	public WiretigerCoreConfig getWtCoreConfig() {
		return coreConfig;
	}
	
	public WiretigerBuilder addEventListener(EventListener listener) {
		this.listeners.add(listener);
		return this;
	}
	
	public WiretigerServerProvider build() {
		return new WiretigerServerProvider(coreConfig, consoleConfig, listeners);
	}

	/** console config **/
	
	public int consoleHttpPort() {
		return consoleConfig.getHttpPort();
	}

	public void consoleHttpPort(int port) {
		consoleConfig.setHttpPort(port);
	}

	public void webRoot(String webRoot) {
		consoleConfig.setWebRoot(webRoot);
	}
	
	public String webRoot() {
		return consoleConfig.getWebRoot();
	}
	
	public void webXmlPath(String webXmlPath) {
		consoleConfig.setWebXmlPath(webXmlPath);
	}
	
	public String webXmlPath() {
		return consoleConfig.getWebXmlPath();
	}

	public int consoleWsPort() {
		return consoleConfig.getWebSocketPort();
	}

	public void consoleWsPort(int port) {
		consoleConfig.setWebSocketPort(port);
	}
}
