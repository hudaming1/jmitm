package org.hum.jmitm.provider;

import org.hum.jmitm.console.http.config.JmitmConsoleConfig;
import org.hum.jmitm.proxy.config.JmitmCoreConfig;
import org.hum.jmitm.proxy.mock.Mock;

public class JmitmBuilder {

	private JmitmCoreConfig coreConfig = new JmitmCoreConfig();
	private JmitmConsoleConfig consoleConfig = new JmitmConsoleConfig();
	
	/**
	 * 为网关代理添加Mock支持
	 * @param mocks
	 */
	public void addMock(Mock... mocks) {
		if (mocks == null || mocks.length == 0) {
			return ;
		}
		for (Mock mock : mocks) {
			this.coreConfig.addMock(mock);
		}
	}
	
	/**
	 * 是否解析HTTPS协议
	 * <pre>
	 *   当传入false时，标记为不对HTTPS协议进行解析，直接透传，同时HTTPS的请求和响应也不会出现在控制台上。
	 *   如果你不希望给客户度安装额外的CA，且只需要解析普通的HTTP请求，可以把这个选项配置为false。
	 * </pre>
	 * @param isParsedHttps
	 */
	public void parseHttps(boolean isParsedHttps) {
		coreConfig.setParseHttps(isParsedHttps);
	}

	public int proxyPort() {
		return coreConfig.getPort();
	}

	/**
	 * 代理使用端口
	 * @param port
	 * @return
	 */
	public JmitmBuilder proxyPort(int port) {
		coreConfig.setPort(port);
		return this;
	}
	
	public int threads() {
		return coreConfig.getThreads();
	}
	
	/**
	 * 代理启用线程数（front使用thread/2，back使用thread/2）
	 * @return
	 */
	public JmitmBuilder threads(int threads) {
		coreConfig.setThreads(threads);
		return this;
	}

	public boolean debug() {
		return coreConfig.isDebug();
	}

	/**
	 * 是否启用debug模式（暂未实现）
	 * @param isDebug
	 * @return
	 */
	public JmitmBuilder debug(boolean isDebug) {
		coreConfig.setDebug(isDebug);
		return this;
	}
	
	public JmitmCoreConfig getWtCoreConfig() {
		return coreConfig;
	}
	
	public JmitmServerProvider build() {
		return new JmitmServerProvider(coreConfig, consoleConfig);
	}

	/** console config **/
	
	public int consoleHttpPort() {
		return consoleConfig.getHttpPort();
	}

	public JmitmBuilder consoleHttpPort(int port) {
		consoleConfig.setHttpPort(port);
		return this;
	}

	public JmitmBuilder webRoot(String webRoot) {
		consoleConfig.setWebRoot(webRoot);
		return this;
	}
	
	public String webRoot() {
		return consoleConfig.getWebRoot();
	}
	
	public JmitmBuilder webXmlPath(String webXmlPath) {
		consoleConfig.setWebXmlPath(webXmlPath);
		return this;
	}
	
	public String webXmlPath() {
		return consoleConfig.getWebXmlPath();
	}

	public int consoleWsPort() {
		return consoleConfig.getWebSocketPort();
	}

	public JmitmBuilder consoleWsPort(int port) {
		consoleConfig.setWebSocketPort(port);
		return this;
	}
	
	public JmitmBuilder pipeHistory(int count) {
		consoleConfig.setPipeHistory(count);
		return this;
	}

	public JmitmBuilder sessionHistory(int count) {
		consoleConfig.setSessionHistory(count);
		return this;
	}
}
