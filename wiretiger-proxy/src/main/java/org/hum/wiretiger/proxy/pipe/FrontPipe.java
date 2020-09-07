package org.hum.wiretiger.proxy.pipe;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;

public class FrontPipe {
	
	private Channel channel;

	public FrontPipe(Channel channel) {
		this.channel = channel;
	}

	public Channel getChannel() {
		return channel;
	}
	
	public String getHost() {
		InetSocketAddress addr = (InetSocketAddress) channel.remoteAddress();
		return addr.getHostString();
	}
	
	public int getPort() {
		InetSocketAddress addr = (InetSocketAddress) channel.remoteAddress();
		return addr.getPort();
	}
}
