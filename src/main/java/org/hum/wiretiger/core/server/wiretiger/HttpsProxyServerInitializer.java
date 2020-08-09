package org.hum.wiretiger.core.server.wiretiger;

import org.hum.wiretiger.core.handler.HttpProxyHandshakeHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class HttpsProxyServerInitializer extends ChannelInitializer<SocketChannel> {

	public HttpsProxyServerInitializer() {
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ch.pipeline().addLast(new HttpProxyHandshakeHandler());
	}
}
