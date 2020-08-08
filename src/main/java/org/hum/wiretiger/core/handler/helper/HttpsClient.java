package org.hum.wiretiger.core.handler.helper;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

public class HttpsClient {

	private static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup(1);
	private static final EventLoopGroup WORKER_GROUP2 = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

	public static Bootstrap newBootStrap() {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(WORKER_GROUP);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
		return bootstrap;
	}
	
	public static FullHttpResponse send(String host, int port, HttpRequest httpRequest) throws Exception {
		final Bootstrap b = newBootStrap();
		Promise<FullHttpResponse> promise = new DefaultPromise<FullHttpResponse>(WORKER_GROUP2.next());
		SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		b.handler(new ClientInit(host, new MainHandler(promise), sslContext, httpRequest));
		b.connect(host, port);
		return promise.get();
	}

	@ChannelHandler.Sharable
	private static class MainHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

		private Promise<FullHttpResponse> promise;

		public MainHandler(Promise<FullHttpResponse> promise) {
			super(false);
			this.promise = promise;
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
			// HttpContent httpContent = (HttpContent) msg;
			// String response = httpContent.content().toString(Charset.defaultCharset());
			promise.setSuccess(msg);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			cause.printStackTrace();
			ctx.channel().close();
		}
	}

	private static class ClientInit extends ChannelInitializer<SocketChannel> {

		private HttpRequest request;
		private ChannelInboundHandler handler;
		private SslContext context;
		private String host;

		public ClientInit(String host, ChannelInboundHandler handler, SslContext context, HttpRequest request) {
			this.host = host;
			this.handler = handler;
			this.context = context;
			this.request = request;
		}

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			// fixbug: jdk8在ssl的handshake时没有sni的bug
			SslHandler newHandler = context.newHandler(ch.alloc(), host, -1);
			newHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
				@Override
				public void operationComplete(Future<? super Channel> future) throws Exception {
					ch.writeAndFlush(request);
				}
			});
			// TODO 根据config判断
			// ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
			ch.pipeline().addLast(newHandler);
			ch.pipeline().addLast(new HttpResponseDecoder());
			ch.pipeline().addLast(new HttpRequestEncoder());
			ch.pipeline().addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
			ch.pipeline().addLast(handler);
		}
	}
}
