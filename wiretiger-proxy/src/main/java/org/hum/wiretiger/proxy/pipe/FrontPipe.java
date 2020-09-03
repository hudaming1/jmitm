package org.hum.wiretiger.proxy.pipe;

import io.netty.channel.Channel;

public class FrontPipe {
	
	private Channel channel;

	public FrontPipe(Channel channel) {
		this.channel = channel;
	}

	public Channel getChannel() {
		return channel;
	}
}
