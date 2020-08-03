package org.hum.wiretiger.core.handler.http;

import org.hum.wiretiger.core.handler.InactiveRelHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class Forward {
	
	private Bootstrap bootStrap = null;
	private String host;
	private int port;
	public Forward(ChannelHandlerContext client2ProxyCtx, String host, int port) {
		this.host = host;
		this.port = port;
		bootStrap = new Bootstrap();
		bootStrap.channel(NioSocketChannel.class);
		bootStrap.group(client2ProxyCtx.channel().eventLoop());
		bootStrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				client2ProxyCtx.channel().pipeline().addLast(new HttpResponseDecoder(), new HttpObjectAggregator(1024 * 1024));
				ch.pipeline().addFirst(new HttpResponseEncoder());
				ch.pipeline().addLast(new ForwardHandler(client2ProxyCtx.channel()), new InactiveRelHandler(client2ProxyCtx.channel()));
			}
		});
	}
	
	public ChannelFuture start() {
		return bootStrap.connect(host, port);
	}
}
