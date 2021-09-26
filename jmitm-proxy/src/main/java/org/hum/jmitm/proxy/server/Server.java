package org.hum.jmitm.proxy.server;

import io.netty.channel.ChannelFuture;

public interface Server {

	public ChannelFuture start();
	
	public void onClose(Object hook);
}
