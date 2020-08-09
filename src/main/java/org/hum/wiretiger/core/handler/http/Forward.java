package org.hum.wiretiger.core.handler.http;

import org.hum.wiretiger.core.pipe.bean.PipeHolder;

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

public class Forward {
	
	private static final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
	private Bootstrap bootStrap = null;
	private String host;
	private int port;
	
	
	public Forward(PipeHolder pipeHolder, String host, int port) {
		this.host = host;
		this.port = port;
		bootStrap = new Bootstrap();
		bootStrap.channel(NioSocketChannel.class);
		bootStrap.group(eventLoopGroup);
		bootStrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel proxy2ServerChannel) throws Exception {
				proxy2ServerChannel.pipeline().addFirst(new HttpRequestEncoder());
				proxy2ServerChannel.pipeline().addLast(new HttpResponseDecoder(), new HttpObjectAggregator(Integer.MAX_VALUE));
				proxy2ServerChannel.pipeline().addLast(new ForwardHandler(pipeHolder));
			}
		});
	}
	
	public ChannelFuture start() {
		return bootStrap.connect(host, port);
	}
}
