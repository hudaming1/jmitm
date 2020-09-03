package org.hum.wiretiger.proxy.pipe.v2;

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
