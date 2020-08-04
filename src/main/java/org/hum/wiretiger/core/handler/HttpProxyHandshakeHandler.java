package org.hum.wiretiger.core.handler;

import org.hum.wiretiger.core.external.pipe_monitor.PipeMonitor;
import org.hum.wiretiger.core.external.pipe_monitor.PipeStatus;
import org.hum.wiretiger.core.external.pipe_monitor.Protocol;
import org.hum.wiretiger.core.handler.bean.HttpRequest;
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
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpProxyHandshakeHandler extends ChannelInboundHandlerAdapter {

	private static final String ConnectedLine = "HTTP/1.1 200 Connection established\r\n\r\n";
	private static final String HTTPS_HANDSHAKE_METHOD = "CONNECT";
	
	@Override
	public void channelRead(ChannelHandlerContext client2ProxyCtx, Object msg) throws Exception {
		HttpRequest request = HttpHelper.decode((ByteBuf) msg);
		PipeMonitor.get().get(client2ProxyCtx.channel()).recordStatus(PipeStatus.Parsed);
		// 区分HTTP和HTTPS
		if (HTTPS_HANDSHAKE_METHOD.equalsIgnoreCase(request.getMethod())) {
			// 根据域名颁发证书
			SslHandler sslHandler = new SslHandler(HttpSslContextFactory.createSSLEngine(request.getHost()));
			// 确保SSL握手完成后，将业务Handler加入pipeline
			sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
				@Override
				public void operationComplete(Future<? super Channel> future) throws Exception {
					client2ProxyCtx.pipeline().addLast(new HttpServerCodec());
					client2ProxyCtx.pipeline().addLast(new HttpServerExpectContinueHandler());
					client2ProxyCtx.pipeline().addLast(new HttpsForwardServerHandler(request.getHost(), request.getPort()));
					PipeMonitor.get().get(client2ProxyCtx.channel()).recordStatus(PipeStatus.Connected);
				}
			});
			client2ProxyCtx.pipeline().addLast(sslHandler);
			client2ProxyCtx.pipeline().remove(this);
			client2ProxyCtx.pipeline().firstContext().writeAndFlush(Unpooled.wrappedBuffer(ConnectedLine.getBytes()));
			PipeMonitor.get().get(client2ProxyCtx.channel()).recordStatus(PipeStatus.Forward);
			PipeMonitor.get().get(client2ProxyCtx.channel()).setProtocol(Protocol.HTTPS.getCode());
		} else {
			PipeMonitor.get().get(client2ProxyCtx.channel()).setProtocol(Protocol.HTTP.getCode());
			// HTTP
			client2ProxyCtx.pipeline().addFirst(new HttpResponseEncoder());
			client2ProxyCtx.pipeline().addLast(new HttpRequestDecoder());
			client2ProxyCtx.pipeline().addLast(new HttpForwardHandler(request.getHost(), request.getPort()));
			client2ProxyCtx.pipeline().remove(this);
			client2ProxyCtx.fireChannelRead(msg);
		}
	}
}
