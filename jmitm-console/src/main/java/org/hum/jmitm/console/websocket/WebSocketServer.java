package org.hum.jmitm.console.websocket;

import org.hum.jmitm.common.util.NamedThreadFactory;
import org.hum.jmitm.common.util.NettyUtils;
import org.hum.jmitm.console.websocket.handler.BusinessServerHandler;
import org.hum.jmitm.console.websocket.handler.WebSocketDecoder;
import org.hum.jmitm.console.websocket.handler.WebSocketEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 使用Netty实现WebSocketServer
 * @author huming
 */
public class WebSocketServer {

	private volatile boolean isStart = false;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private ServerBootstrap bootstrap;
	private int port;

	public WebSocketServer(int port) {
		this.port = port;
		this.bossGroup = NettyUtils.initEventLoopGroup(1, new NamedThreadFactory("ws-boss-thread"));
		this.workerGroup = NettyUtils.initEventLoopGroup(4, new NamedThreadFactory("ws-worker-thread"));
		this.bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup);
		bootstrap.channel(NettyUtils.isSupportNativeET() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
		bootstrap.childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new HttpServerCodec());
				ch.pipeline().addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
				ch.pipeline().addLast(new IdleStateHandler(60, 5, 0));
				ch.pipeline().addLast(new WebSocketEncoder());
				ch.pipeline().addLast(new WebSocketDecoder());
				ch.pipeline().addLast(new BusinessServerHandler());
			}
		});
	}

	public synchronized ChannelFuture start() {
		if (isStart) {
			throw new IllegalStateException("websocket-server has been started.");
		}
		isStart = true;
		return bootstrap.bind(this.port);
	}

	public int getPort() {
		return port;
	}
}
