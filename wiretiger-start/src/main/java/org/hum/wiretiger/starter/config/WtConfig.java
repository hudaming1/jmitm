package org.hum.wiretiger.starter.config;

import org.hum.wiretiger.console.http.config.WtConsoleConfig;
import org.hum.wiretiger.proxy.config.WtCoreConfig;
import org.hum.wiretiger.proxy.mock.Mock;

public class WtConfig {

	private WtCoreConfig coreConfig = new WtCoreConfig();
	private WtConsoleConfig consoleConfig = new WtConsoleConfig();
	
	public void addMock(Mock... mocks) {
		if (mocks == null || mocks.length == 0) {
			return ;
		}
		for (Mock mock : mocks) {
			this.coreConfig.addMock(mock);
		}
	}

	public int getProxyPort() {
		return coreConfig.getPort();
	}

	public void setProxyPort(int port) {
		coreConfig.setPort(port);
	}
	
	public int getThreads() {
		return coreConfig.getThreads();
	}

	public void setThreads(int threads) {
		coreConfig.setThreads(threads);
	}

	public boolean isDebug() {
		return coreConfig.isDebug();
	}

	public void setDebug(boolean isDebug) {
		coreConfig.setDebug(isDebug);
	}
	
	public WtCoreConfig getWtCoreConfig() {
		return coreConfig;
	}

	/** console config **/
	
	public int getConsoleHttpPort() {
		return consoleConfig.getHttpPort();
	}

	public void setConsoleHttpPort(int port) {
		consoleConfig.setHttpPort(port);
	}

	public void setWebRoot(String webRoot) {
		consoleConfig.setWebRoot(webRoot);
	}
	
	public String getWebRoot() {
		return consoleConfig.getWebRoot();
	}
	
	public void setWebXmlPath(String webXmlPath) {
		consoleConfig.setWebXmlPath(webXmlPath);
	}
	
	public String getWebXmlPath() {
		return consoleConfig.getWebXmlPath();
	}

	public int getConsoleWsPort() {
		return consoleConfig.getWebSocketPort();
	}

	public void setConsoleWsPort(int port) {
		consoleConfig.setWebSocketPort(port);
	}
}