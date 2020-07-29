package org.hum.wiretiger.core.handler;

import org.hum.wiretiger.core.server.impl.HttpsForwardServerHandler;
import org.hum.wiretiger.core.server.impl.HttpsProxyServerInitializer.Forward;

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
	
	/**
	 * 这里的msg是CONNECT方法头
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String[] req = parse2Domain((ByteBuf) msg);

		if (req[0].equalsIgnoreCase("CONNECT")) {
			// 根据域名颁发证书
			SslHandler sslHandler = new SslHandler(HttpSslContextFactory.createSSLEngine(req[1]));
			// 确保SSL握手完成后，将业务Handler加入pipeline
			sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
				@Override
				public void operationComplete(Future<? super Channel> future) throws Exception {
					ctx.pipeline().addLast(new HttpServerCodec());
					ctx.pipeline().addLast(new HttpServerExpectContinueHandler());
					ctx.pipeline().addLast(new HttpsForwardServerHandler(req[1], 443));
				}
			});
			ctx.pipeline().addLast("sslHandler", sslHandler);
			ctx.pipeline().remove(this);

			// 注意：这里要用first啊，pipeline顺序不要错
			ctx.pipeline().firstContext().writeAndFlush(Unpooled.wrappedBuffer(ConnectedLine.getBytes()))
					.addListener(new GenericFutureListener<Future<? super Void>>() {
						@Override
						public void operationComplete(Future<? super Void> future) throws Exception {
							System.out.println("flush connect-line");
						}
					});
		} else {
			// 建立远端转发连接（远端收到响应后，一律转发给本地）
			Forward forward = new Forward(ctx, req[1], 80);
			forward.start().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture remoteFuture) throws Exception {
					// forward request
					remoteFuture.channel().pipeline().firstContext().writeAndFlush(msg);
					System.err.println("=============HTTP_REQUEST_BEGIN=============");
					// TODO 没有刷出去，还是响应我没有解码？
					System.err.println(msg);
					System.err.println("=============HTTP_REQUEST_END=============");
				}
			});
		}
	}
}
