package org.hum.wiretiger.core.server;

public interface WireTigerServer {

	public void start();
	
	public void onClose(Object hook);
}
