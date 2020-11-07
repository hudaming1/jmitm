package org.hum.wiredog.proxy.server;

import io.netty.channel.ChannelFuture;

public interface WtServer {

	public ChannelFuture start();
	
	public void onClose(Object hook);
}
