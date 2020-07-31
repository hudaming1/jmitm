package org.hum.wiretiger.core.handler;

import org.hum.wiretiger.core.external.conmonitor.ConnectMonitor;
import org.hum.wiretiger.core.external.conmonitor.ConnectionStatus;
import org.hum.wiretiger.core.handler.bean.HttpRequest;
import org.hum.wiretiger.core.handler.helper.HttpHelper;
import org.hum.wiretiger.core.handler.http.Forward;
import org.hum.wiretiger.core.handler.http.HttpForwardHandler;
import org.hum.wiretiger.core.handler.https.HttpsForwardServerHandler;
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
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpProxyHandshakeHandler extends ChannelInboundHandlerAdapter {

	private static final String ConnectedLine = "HTTP/1.1 200 Connection established\r\n\r\n";
	
	@Override
	public void channelRead(ChannelHandlerContext sourceCtx, Object msg) throws Exception {
		// TODO 这里解析的port，最好从clientCtx中获取
		HttpRequest request = HttpHelper.decode((ByteBuf) msg);
		sourceCtx.channel().attr(AttributeKey.valueOf(ConnectionStatus.STATUS)).set(ConnectionStatus.Parsed);

		if (request.getMethod().equalsIgnoreCase("CONNECT")) {
			// 根据域名颁发证书
			SslHandler sslHandler = new SslHandler(HttpSslContextFactory.createSSLEngine(request.getHost()));
			// 确保SSL握手完成后，将业务Handler加入pipeline
			sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
				@Override
				public void operationComplete(Future<? super Channel> future) throws Exception {
					sourceCtx.pipeline().addLast(new HttpServerCodec());
					sourceCtx.pipeline().addLast(new HttpServerExpectContinueHandler());
					sourceCtx.pipeline().addLast(new HttpsForwardServerHandler(request.getHost(), request.getPort()));
					sourceCtx.channel().attr(AttributeKey.valueOf(ConnectionStatus.STATUS)).set(ConnectionStatus.Connected);
				}
			});
			sourceCtx.pipeline().addLast("sslHandler", sslHandler);
			sourceCtx.pipeline().remove(this);
			sourceCtx.pipeline().firstContext().writeAndFlush(Unpooled.wrappedBuffer(ConnectedLine.getBytes()));
			sourceCtx.channel().attr(AttributeKey.valueOf(ConnectionStatus.STATUS)).set(ConnectionStatus.Flushed);
		} else {
			sourceCtx.pipeline().addLast(new HttpServerCodec());
			sourceCtx.pipeline().addLast(new HttpForwardHandler(request.getHost(), request.getPort()));
			sourceCtx.fireChannelRead(msg);
		}
	}
}
