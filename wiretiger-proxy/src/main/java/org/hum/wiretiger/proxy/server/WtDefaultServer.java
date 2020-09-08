package org.hum.wiretiger.proxy.server;

import java.util.List;

import org.hum.wiretiger.common.exception.WiretigerException;
import org.hum.wiretiger.common.util.NamedThreadFactory;
import org.hum.wiretiger.proxy.config.WtCoreConfig;
import org.hum.wiretiger.proxy.facade.event.EventListener;
import org.hum.wiretiger.proxy.pipe.ProxyHandshakeHandler;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;
import org.hum.wiretiger.proxy.util.NettyUtils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WtDefaultServer implements WtServer {
	
	private WtCoreConfig config;
	
	private List<EventListener> listeners;

	public WtDefaultServer(WtCoreConfig config) {
		this.config = config;
	}

	@Override
	public void start() {
		EventHandler eventHandler = new EventHandler();
		
		// regist event
		if (listeners != null && !listeners.isEmpty()) {
			eventHandler.addAll(listeners);
		}
		
		// Configure the server.
		EventLoopGroup bossGroup = NettyUtils.initEventLoopGroup(1, new NamedThreadFactory("wt-boss-thread"));
		EventLoopGroup masterThreadPool = NettyUtils.initEventLoopGroup(config.getThreads(), new NamedThreadFactory("wt-worker-thread"));
		ProxyHandshakeHandler httpProxyHandshakeHandler = new ProxyHandshakeHandler(eventHandler);
		try {
			ServerBootstrap bootStrap = new ServerBootstrap();
			bootStrap.option(ChannelOption.SO_BACKLOG, 1024);
			bootStrap.group(bossGroup, masterThreadPool).channel(NioServerSocketChannel.class);
			if (config.isDebug()) {
				bootStrap.handler(new LoggingHandler(LogLevel.DEBUG));
			}
			bootStrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) {
					ch.pipeline().addLast(new HttpResponseEncoder(), new HttpRequestDecoder() , httpProxyHandshakeHandler);
				}
			});

			Channel ch = bootStrap.bind(config.getPort()).sync().channel();
			log.info("wire_tiger server started on port:" + config.getPort());

			ch.closeFuture().sync();
		} catch (Exception e) {
			log.error("start occur error, config=" + config, e);
			throw new WiretigerException("WtDefaultServer start failed.", e);
		} finally {
			bossGroup.shutdownGracefully();
			masterThreadPool.shutdownGracefully();
		}
	}
	
	@Override
	public void onClose(Object hook) {
		
	}

	public void setListeners(List<EventListener> listeners) {
		this.listeners = listeners;
	}
}
