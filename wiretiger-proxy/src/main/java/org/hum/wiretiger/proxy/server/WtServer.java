package org.hum.wiretiger.proxy.server;

public interface WtServer {

	public void start();
	
	public void onClose(Object hook);
}
