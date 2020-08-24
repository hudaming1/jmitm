package org.hum.wiretiger.core.pipe;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
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
public class HttpOrHttpsForward {
	
	private static final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
	private static SslContext SsslContext;
	private Bootstrap bootStrap = null;
	private String host;
	private int port;
	private boolean isHttps;
	private SslHandler sslHandler;
	static {
		try {
			SsslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			log.info("init ssl_context success...");
		} catch (Exception ce) {
			log.error("init ssl_context failed", ce);
		}
	}

	public HttpOrHttpsForward(ChannelDuplexHandler duplexHandler, String host, int port) {
		this(duplexHandler, host, port, false);
	}
	
	public HttpOrHttpsForward(ChannelDuplexHandler duplexHandler, String host, int port, boolean isHttps) {
		this.isHttps = isHttps;
		this.host = host;
		this.port = port;
		bootStrap = new Bootstrap();
		bootStrap.channel(NioSocketChannel.class);
		bootStrap.group(eventLoopGroup);
		if (isHttps) {
			bootStrap.handler(new ChannelInitializer<SocketChannel> () {
				@Override
				protected void initChannel(SocketChannel proxy2ServerChannel) throws Exception {
					sslHandler = SsslContext.newHandler(proxy2ServerChannel.alloc(), host, -1);
					// TODO 根据config判断
					// ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
					proxy2ServerChannel.pipeline().addLast(sslHandler);
					proxy2ServerChannel.pipeline().addLast(new HttpResponseDecoder());
					proxy2ServerChannel.pipeline().addLast(new HttpRequestEncoder(), new HttpObjectAggregator(Integer.MAX_VALUE));
					proxy2ServerChannel.pipeline().addLast(duplexHandler);
				}
			});
		} else {
			bootStrap.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel proxy2ServerChannel) throws Exception {
					proxy2ServerChannel.pipeline().addFirst(new HttpRequestEncoder());
					proxy2ServerChannel.pipeline().addLast(new HttpResponseDecoder(), new HttpObjectAggregator(Integer.MAX_VALUE));
					proxy2ServerChannel.pipeline().addLast(duplexHandler);
				}
			});
		}
	}
	
	public Future<?> start() throws InterruptedException {
		if (isHttps) {
			bootStrap.connect(host, port).sync();
			return sslHandler.handshakeFuture();
		} else {
			return bootStrap.connect(host, port);
		}
	}
}
