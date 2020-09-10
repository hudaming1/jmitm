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
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BackPipe {

	private static final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
	private static SslContext SsslContext;
	private String host;
	private int port;
	private Bootstrap bootStrap = null;
	private Channel channel;
	private SslHandler sslHandler;
	private volatile boolean acitve = false;
	static {
		try {
			SsslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			log.info("init ssl_context success...");
		} catch (Exception ce) {
			log.error("init ssl_context failed", ce);
		}
	}

	public BackPipe(String host, int port, boolean isHttps) {
		this.host = host;
		this.port = port;
		if (isHttps) {
			initHttpsBackPipe(host, port);
		} else {
			initHttpBackPipe(host, port);
		}
	}
	
	private void initHttpsBackPipe(String host, int port) {
		bootStrap = new Bootstrap();
		bootStrap.channel(NioSocketChannel.class);
		bootStrap.group(eventLoopGroup);
		bootStrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel proxy2ServerChannel) throws Exception {
				sslHandler = SsslContext.newHandler(proxy2ServerChannel.alloc(), host, -1);
				proxy2ServerChannel.pipeline().addLast(sslHandler);
				proxy2ServerChannel.pipeline().addLast(new HttpResponseDecoder());
				proxy2ServerChannel.pipeline().addLast(new HttpRequestEncoder(), new HttpObjectAggregator(Integer.MAX_VALUE));
			}
		});
	}
	
	private void initHttpBackPipe(String host, int port) {
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
	
	public boolean isActive() {
		return acitve;
	}

	public ChannelFuture connect() {
		acitve = true;
		ChannelFuture channelFuture = bootStrap.connect(host, port);
		this.channel = channelFuture.channel();
		return channelFuture;
	}
	
	public Future<Channel> handshakeFuture() {
		return sslHandler.handshakeFuture();
	}

	public Channel getChannel() {
		return this.channel;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}
