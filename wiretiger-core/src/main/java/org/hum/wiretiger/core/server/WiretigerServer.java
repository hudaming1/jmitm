package org.hum.wiretiger.core.server;

public interface WiretigerServer {

	public void start();
	
	public void onClose(Object hook);
}
