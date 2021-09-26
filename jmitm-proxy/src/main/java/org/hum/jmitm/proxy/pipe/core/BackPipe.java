package org.hum.jmitm.proxy.pipe.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
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

	public BackPipe(EventLoopGroup eventLoopGroup, String host, int port, boolean isHttps) {
		this.host = host;
		this.port = port;
		if (isHttps) {
			initHttpsBackPipe(eventLoopGroup, host, port);
		} else {
			initHttpBackPipe(eventLoopGroup, host, port);
		}
	}
	
	private void initHttpsBackPipe(EventLoopGroup eventLoopGroup, String host, int port) {
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
	
	private void initHttpBackPipe(EventLoopGroup eventLoopGroup, String host, int port) {
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
