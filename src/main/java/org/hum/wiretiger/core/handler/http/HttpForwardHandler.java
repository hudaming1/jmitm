package org.hum.wiretiger.core.handler.http;

import org.hum.wiretiger.core.external.conmonitor.ConnectMonitor;
import org.hum.wiretiger.core.external.conmonitor.ConnectionStatus;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.AttributeKey;

public class HttpForwardHandler extends SimpleChannelInboundHandler<HttpObject> {
	
	private String host;
	private int port;
	
	public HttpForwardHandler(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead0(ChannelHandlerContext sourceCtx, HttpObject msg) throws Exception {
		System.out.println("HttpForwardHandler.requet=" + msg);
		// 建立远端转发连接（远端收到响应后，一律转发给本地）
		new Forward(sourceCtx, host, port).start().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture targetChannelFuture) throws Exception {
				sourceCtx.channel().attr(AttributeKey.valueOf(ConnectionStatus.STATUS)).set(ConnectionStatus.Connected);
				// forward request
				targetChannelFuture.channel().pipeline().firstContext().writeAndFlush(msg);
				sourceCtx.channel().attr(AttributeKey.valueOf(ConnectionStatus.STATUS)).set(ConnectionStatus.Forward);
				sourceCtx.channel().attr(AttributeKey.valueOf(ConnectMonitor.REQ_ATTR_NAME)).set(msg);
//							System.err.println("=============HTTP_REQUEST_BEGIN=============");
//							System.err.println(msg);
//							System.err.println("=============HTTP_REQUEST_END=============");
			}
		});
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
