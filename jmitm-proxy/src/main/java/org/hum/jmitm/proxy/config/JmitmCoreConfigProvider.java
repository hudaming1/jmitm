package org.hum.jmitm.proxy.config;

import java.util.Collections;
import java.util.List;

import org.hum.jmitm.proxy.mock.Mock;

public class JmitmCoreConfigProvider {

	private JmitmCoreConfig wiredogCoreConfig;
	
	private JmitmCoreConfigProvider(JmitmCoreConfig wiredogCoreConfig) {
		this.wiredogCoreConfig = wiredogCoreConfig;
	}
	
	private static JmitmCoreConfigProvider provider;
	
	public static synchronized JmitmCoreConfigProvider init(JmitmCoreConfig wiredogCoreConfig) {
		if (provider != null) {
			throw new IllegalStateException("provider has been initilized");
		} else if (provider == null) {
			provider = new JmitmCoreConfigProvider(wiredogCoreConfig);
		}
		return provider;
	}
	
	public static JmitmCoreConfigProvider get() {
		if (provider == null) {
			throw new IllegalStateException("provider has not been initilize");
		}
		return provider;
	}
	
	public int getPort() {
		return this.wiredogCoreConfig.getPort();
	}
	
	public int getThreads() {
		return this.wiredogCoreConfig.getThreads();
	}
	
	public List<Mock> getMockList() {
		return Collections.unmodifiableList(this.wiredogCoreConfig.getMockList());
	}
	
	public boolean isParseHttps() {
		return this.wiredogCoreConfig.isParseHttps();
	}
	
	public void setParseHttps(boolean isParseHttps) {
		this.wiredogCoreConfig.setParseHttps(isParseHttps);
	}
	
	public boolean isOpenMasterMockStwich() {
		return this.wiredogCoreConfig.isMasterMockStwich();
	}
	
	public void setOpenMasterMockStwich(boolean value) {
		this.wiredogCoreConfig.setMasterMockStwich(value);
	}

	public boolean isDebug() {
		return this.wiredogCoreConfig.isDebug();
	}
	
	@Override
	public String toString() {
		return this.wiredogCoreConfig.toString();
	}
}
