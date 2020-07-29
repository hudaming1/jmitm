package org.hum.wiretiger.core.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Forward {
	
	private Bootstrap bootStrap = null;
	private String host;
	private int port;
	public Forward(ChannelHandlerContext ctx, String host, int port) {
		this.host = host;
		this.port = port;
		bootStrap = new Bootstrap();
		bootStrap.channel(NioSocketChannel.class);
		bootStrap.group(ctx.channel().eventLoop());
		bootStrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new ForwardHandler(ctx.channel()), new InactiveHandler(ctx.channel()));
			}
		});
	}
	
	public ChannelFuture start() {
		return bootStrap.connect(host, port);
	}
}
