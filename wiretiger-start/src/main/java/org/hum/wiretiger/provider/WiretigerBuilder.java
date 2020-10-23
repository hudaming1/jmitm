package org.hum.wiretiger.provider;

import org.hum.wiretiger.console.http.config.WiretigerConsoleConfig;
import org.hum.wiretiger.proxy.config.WiretigerCoreConfig;
import org.hum.wiretiger.proxy.mock.Mock;

public class WiretigerBuilder {

	private WiretigerCoreConfig coreConfig = new WiretigerCoreConfig();
	private WiretigerConsoleConfig consoleConfig = new WiretigerConsoleConfig();
	
	public void addMock(Mock... mocks) {
		if (mocks == null || mocks.length == 0) {
			return ;
		}
		for (Mock mock : mocks) {
			this.coreConfig.addMock(mock);
		}
	}
	
	public void parseHttps(boolean val) {
		coreConfig.setParseHttps(val);
	}

	public int proxyPort() {
		return coreConfig.getPort();
	}

	public WiretigerBuilder proxyPort(int port) {
		coreConfig.setPort(port);
		return this;
	}
	
	public int threads() {
		return coreConfig.getThreads();
	}

	public WiretigerBuilder threads(int threads) {
		coreConfig.setThreads(threads);
		return this;
	}

	public boolean debug() {
		return coreConfig.isDebug();
	}

	public WiretigerBuilder debug(boolean isDebug) {
		coreConfig.setDebug(isDebug);
		return this;
	}
	
	public WiretigerCoreConfig getWtCoreConfig() {
		return coreConfig;
	}
	
	public WiretigerServerProvider build() {
		return new WiretigerServerProvider(coreConfig, consoleConfig);
	}

	/** console config **/
	
	public int consoleHttpPort() {
		return consoleConfig.getHttpPort();
	}

	public WiretigerBuilder consoleHttpPort(int port) {
		consoleConfig.setHttpPort(port);
		return this;
	}

	public WiretigerBuilder webRoot(String webRoot) {
		consoleConfig.setWebRoot(webRoot);
		return this;
	}
	
	public String webRoot() {
		return consoleConfig.getWebRoot();
	}
	
	public WiretigerBuilder webXmlPath(String webXmlPath) {
		consoleConfig.setWebXmlPath(webXmlPath);
		return this;
	}
	
	public String webXmlPath() {
		return consoleConfig.getWebXmlPath();
	}

	public int consoleWsPort() {
		return consoleConfig.getWebSocketPort();
	}

	public WiretigerBuilder consoleWsPort(int port) {
		consoleConfig.setWebSocketPort(port);
		return this;
	}
	
	public WiretigerBuilder pipeHistory(int count) {
		consoleConfig.setPipeHistory(count);
		return this;
	}

	public WiretigerBuilder sessionHistory(int count) {
		consoleConfig.setSessionHistory(count);
		return this;
	}
}
