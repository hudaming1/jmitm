package org.hum.wiretiger.proxy.pipe;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

public class BackPipe {

	private static final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
	private String host;
	private int port;
	private Bootstrap bootStrap = null;
	private Channel channel;

	public BackPipe(String host, int port) {
		this.host = host;
		this.port = port;
		bootStrap = new Bootstrap();
		bootStrap.channel(NioSocketChannel.class);
		bootStrap.group(eventLoopGroup);
		bootStrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel proxy2ServerChannel) throws Exception {
				proxy2ServerChannel.pipeline().addLast(new HttpResponseDecoder());
				proxy2ServerChannel.pipeline().addLast(new HttpRequestEncoder(), new HttpObjectAggregator(Integer.MAX_VALUE));
			}
		});
	}

	public ChannelFuture connect() throws InterruptedException {
		System.out.println("connect " + host + ":" + port);
		ChannelFuture channelFuture = bootStrap.connect(host, port).sync();
		this.channel = channelFuture.channel();
		return channelFuture;
	}

	public Channel getChannel() {
		return this.channel;
	}
}
