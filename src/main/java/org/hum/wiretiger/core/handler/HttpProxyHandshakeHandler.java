package org.hum.wiretiger.core.handler;

import org.hum.wiretiger.core.external.conmonitor.ConnectionStatus;
import org.hum.wiretiger.core.handler.bean.HttpRequest;
import org.hum.wiretiger.core.handler.bean.Pipe;
import org.hum.wiretiger.core.handler.helper.HttpHelper;
import org.hum.wiretiger.core.handler.http.HttpForwardHandler;
import org.hum.wiretiger.core.handler.https.HttpsForwardServerHandler;
import org.hum.wiretiger.core.ssl.HttpSslContextFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpProxyHandshakeHandler extends ChannelInboundHandlerAdapter {

	private static final String ConnectedLine = "HTTP/1.1 200 Connection established\r\n\r\n";
	
	@Override
	public void channelRead(ChannelHandlerContext client2ProxyCtx, Object msg) throws Exception {
		HttpRequest request = HttpHelper.decode((ByteBuf) msg);
		((Pipe) client2ProxyCtx.channel().attr(AttributeKey.valueOf(Pipe.PIPE_ATTR_NAME)).get()).setStatus(ConnectionStatus.Parsed);
		// 区分HTTP和HTTPS
		if (request.getMethod().equalsIgnoreCase("CONNECT")) {
			// 根据域名颁发证书
			SslHandler sslHandler = new SslHandler(HttpSslContextFactory.createSSLEngine(request.getHost()));
			// 确保SSL握手完成后，将业务Handler加入pipeline
			sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
				@Override
				public void operationComplete(Future<? super Channel> future) throws Exception {
					client2ProxyCtx.pipeline().addLast(new HttpServerCodec());
					client2ProxyCtx.pipeline().addLast(new HttpServerExpectContinueHandler());
					client2ProxyCtx.pipeline().addLast(new HttpsForwardServerHandler(request.getHost(), request.getPort()));
					((Pipe) client2ProxyCtx.channel().attr(AttributeKey.valueOf(Pipe.PIPE_ATTR_NAME)).get()).setStatus(ConnectionStatus.Connected);
				}
			});
			client2ProxyCtx.pipeline().addLast("sslHandler", sslHandler);
			client2ProxyCtx.pipeline().remove(this);
			client2ProxyCtx.pipeline().firstContext().writeAndFlush(Unpooled.wrappedBuffer(ConnectedLine.getBytes()));
			((Pipe) client2ProxyCtx.channel().attr(AttributeKey.valueOf(Pipe.PIPE_ATTR_NAME)).get()).setStatus(ConnectionStatus.Flushed);
		} else {
			// HTTP
			client2ProxyCtx.pipeline().addFirst(new HttpResponseEncoder());
			client2ProxyCtx.pipeline().addLast(new HttpRequestDecoder());
			client2ProxyCtx.pipeline().addLast(new HttpForwardHandler(request.getHost(), request.getPort()));
			client2ProxyCtx.pipeline().remove(this);
			client2ProxyCtx.fireChannelRead(msg);
		}
	}
}
