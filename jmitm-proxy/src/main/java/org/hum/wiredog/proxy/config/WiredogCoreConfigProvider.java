package org.hum.wiredog.proxy.config;

import java.util.Collections;
import java.util.List;

import org.hum.wiredog.proxy.mock.Mock;

public class WiredogCoreConfigProvider {

	private WiredogCoreConfig wiredogCoreConfig;
	
	private WiredogCoreConfigProvider(WiredogCoreConfig wiredogCoreConfig) {
		this.wiredogCoreConfig = wiredogCoreConfig;
	}
	
	private static WiredogCoreConfigProvider provider;
	
	public static synchronized WiredogCoreConfigProvider init(WiredogCoreConfig wiredogCoreConfig) {
		if (provider != null) {
			throw new IllegalStateException("provider has been initilized");
		} else if (provider == null) {
			provider = new WiredogCoreConfigProvider(wiredogCoreConfig);
		}
		return provider;
	}
	
	public static WiredogCoreConfigProvider get() {
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
