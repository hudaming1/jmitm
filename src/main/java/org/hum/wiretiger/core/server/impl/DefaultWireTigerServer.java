package org.hum.wiretiger.core.server.impl;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.hum.wiretiger.config.WireTigerConfig;
import org.hum.wiretiger.core.server.WireTigerServer;
import org.hum.wiretiger.exception.WireTigerException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultWireTigerServer implements WireTigerServer {

	static {
		try {
			Security.addProvider(new BouncyCastleProvider());
			log.info("finish init BC");
		} catch (Exception e) {
			log.error("DefaultWireTigerServer init BC error", e);
			System.exit(-1);
		}
	}
	
	private WireTigerConfig config;

	public DefaultWireTigerServer(WireTigerConfig config) {
		this.config = config;
	}

	@Override
	public void start() {
		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup masterThreadPool = new NioEventLoopGroup(config.getThreads());
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, masterThreadPool).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO));
			b.childHandler(new HttpsProxyServerInitializer());

			Channel ch = b.bind(config.getPort()).sync().channel();


			ch.closeFuture().sync();
		} catch (Exception e) {
			log.error("start occur error, config=" + config, e);
			throw new WireTigerException("DefaultWireTigerServer start failed.", e);
		} finally {
			bossGroup.shutdownGracefully();
			masterThreadPool.shutdownGracefully();
		}
	}

	@Override
	public void onClose(Object hook) {
		
	}
}
