package org.hum.wiretiger.ws.wrapper;

import org.hum.wiretiger.common.wrapper.ChannelWrapper;

import io.netty.channel.Channel;

public class ConsoleChannelWrapper extends ChannelWrapper {

	public ConsoleChannelWrapper(Channel channel) {
		super(channel);
	}
}