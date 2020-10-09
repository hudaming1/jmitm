package org.hum.wiretiger.proxy.server;

import io.netty.channel.ChannelFuture;

public interface WtServer {

	public ChannelFuture start();
	
	public void onClose(Object hook);
}
