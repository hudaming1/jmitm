package org.hum.wiretiger.core.server.wiretiger;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.hum.wiretiger.common.exception.WiretigerException;
import org.hum.wiretiger.config.WiretigerConfig;
import org.hum.wiretiger.core.server.WiretigerServer;
import org.hum.wiretiger.core.server.console.ConsoleServer;

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
public class DefaultWireTigerServer implements WiretigerServer {

	static {
		try {
			Security.addProvider(new BouncyCastleProvider());
			log.info("finish init BC");
		} catch (Exception e) {
			log.error("DefaultWireTigerServer init BC error", e);
			System.exit(-1);
		}
	}
	
	private WiretigerConfig config;

	public DefaultWireTigerServer(WiretigerConfig config) {
		this.config = config;
	}

	@Override
	public void start() {
		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup masterThreadPool = new NioEventLoopGroup(config.getThreads());
		try {
			ServerBootstrap bootStrap = new ServerBootstrap();
			bootStrap.option(ChannelOption.SO_BACKLOG, 1024);
			bootStrap.group(bossGroup, masterThreadPool).channel(NioServerSocketChannel.class);
			if (config.isDebug()) {
				bootStrap.handler(new LoggingHandler(LogLevel.INFO));
			}
			bootStrap.childHandler(new HttpsProxyServerInitializer());

			Channel ch = bootStrap.bind(config.getPort()).sync().channel();
			log.info("wire_tiger server started on port:" + config.getPort());
			
			// 启动控制台
			if (config.getConsolePort() != null) {
				startConsole(config.getConsolePort());
			}

			ch.closeFuture().sync();
		} catch (Exception e) {
			log.error("start occur error, config=" + config, e);
			throw new WiretigerException("DefaultWireTigerServer start failed.", e);
		} finally {
			bossGroup.shutdownGracefully();
			masterThreadPool.shutdownGracefully();
		}
	}
	
	private void startConsole(int port) throws Exception {
		ConsoleServer.startJetty(port);
		log.info("console server started on port:" + port);
	}

	@Override
	public void onClose(Object hook) {
		
	}
}
