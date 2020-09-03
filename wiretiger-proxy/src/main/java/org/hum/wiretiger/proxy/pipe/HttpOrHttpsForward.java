package org.hum.wiretiger.proxy.pipe;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeHolder;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;
import org.hum.wiretiger.proxy.pipe.enumtype.Protocol;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
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
import io.netty.util.concurrent.GenericFutureListener;
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
	private EventHandler eventHandler;
	private WtPipeHolder pipeHolder;
	static {
		try {
			SsslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			log.info("init ssl_context success...");
		} catch (Exception ce) {
			log.error("init ssl_context failed", ce);
		}
	}
	
	public HttpOrHttpsForward(ChannelDuplexHandler duplexHandler, String host, int port, WtPipeHolder pipeHolder, EventHandler eventHandler) {
		this.pipeHolder = pipeHolder;
		this.eventHandler = eventHandler;
		this.isHttps = pipeHolder.getProtocol() == Protocol.HTTPS;
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
					proxy2ServerChannel.pipeline().addLast(new HttpResponseDecoder());
					proxy2ServerChannel.pipeline().addLast(new HttpRequestEncoder(), new HttpObjectAggregator(Integer.MAX_VALUE));
					proxy2ServerChannel.pipeline().addLast(duplexHandler);
				}
			});
		}
	}
	
	public Future<?> start(Channel clientChannel) throws InterruptedException {
		if (isHttps) {
			bootStrap.connect(host, port).addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (!future.isSuccess()) {
						future.channel().close();
						clientChannel.close();
						pipeHolder.recordStatus(PipeStatus.Error);
						pipeHolder.addEvent(PipeEventType.Error, "建立连接错误：" + future.cause().getMessage());
						eventHandler.fireErrorEvent(pipeHolder);
						log.error(host + ":" + port + " connect failed, cause:" + future.cause().getMessage());
					}
				}
			}).await();
			log.info(host + ":" + port + "await");
			return sslHandler.handshakeFuture();
		} else {
			return bootStrap.connect(host, port).addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (!future.isSuccess()) {
						future.channel().close();
						clientChannel.close();
						pipeHolder.recordStatus(PipeStatus.Error);
						pipeHolder.addEvent(PipeEventType.Error, "建立连接错误：" + future.cause().getMessage());
						eventHandler.fireErrorEvent(pipeHolder);
						log.error(host + ":" + port + " connect failed, cause:" + future.cause().getMessage());
					}
				}
			});
		}
	}
}
