package org.hum.wiretiger.core.handler;

import org.hum.wiretiger.core.handler.bean.HttpRequest;
import org.hum.wiretiger.core.handler.helper.HttpHelper;
import org.hum.wiretiger.core.ssl.HttpSslContextFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpProxyHandshakeHandler extends ChannelInboundHandlerAdapter {

	private static final String ConnectedLine = "HTTP/1.1 200 Connection established\r\n\r\n";
	
	/**
	 * 这里的msg是CONNECT方法头
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		HttpRequest request = HttpHelper.decode((ByteBuf) msg);

		if (request.getMethod().equalsIgnoreCase("CONNECT")) {
			// 根据域名颁发证书
			SslHandler sslHandler = new SslHandler(HttpSslContextFactory.createSSLEngine(request.getHost()));
			// 确保SSL握手完成后，将业务Handler加入pipeline
			sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
				@Override
				public void operationComplete(Future<? super Channel> future) throws Exception {
					ctx.pipeline().addLast(new HttpServerCodec());
					ctx.pipeline().addLast(new HttpServerExpectContinueHandler());
					ctx.pipeline().addLast(new HttpsForwardServerHandler(request.getHost(), request.getPort()));
				}
			});
			ctx.pipeline().addLast("sslHandler", sslHandler);
			ctx.pipeline().remove(this);
			ctx.pipeline().firstContext().writeAndFlush(Unpooled.wrappedBuffer(ConnectedLine.getBytes()));
		} else {
			// 建立远端转发连接（远端收到响应后，一律转发给本地）
			new Forward(ctx, request.getHost(), request.getPort()).start().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture remoteFuture) throws Exception {
					// forward request
					remoteFuture.channel().pipeline().firstContext().writeAndFlush(msg);
					System.err.println("=============HTTP_REQUEST_BEGIN=============");
					System.err.println(msg);
					System.err.println("=============HTTP_REQUEST_END=============");
				}
			});
		}
	}
}
