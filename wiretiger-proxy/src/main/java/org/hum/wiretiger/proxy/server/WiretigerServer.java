package org.hum.wiretiger.proxy.server;

public interface WiretigerServer {

	public void start();
	
	public void onClose(Object hook);
}
