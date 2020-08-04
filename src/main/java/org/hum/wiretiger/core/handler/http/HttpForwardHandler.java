package org.hum.wiretiger.core.handler.http;

import org.hum.wiretiger.core.external.pipe_monitor.PipeMonitor;
import org.hum.wiretiger.core.external.pipe_monitor.PipeStatus;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpObject;

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
	public void channelRead0(ChannelHandlerContext sourceCtx, HttpObject clientRequest) throws Exception {
		// 建立远端转发连接（远端收到响应后，一律转发给本地）
		new Forward(sourceCtx, host, port).start().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture proxy2RemoteFuture) throws Exception {
				PipeMonitor.get().get(sourceCtx.channel()).setStatus(PipeStatus.Connected);
				// forward request
				proxy2RemoteFuture.channel().writeAndFlush(clientRequest);
				PipeMonitor.get().get(sourceCtx.channel()).setStatus(PipeStatus.Forward);
				if (clientRequest instanceof DefaultHttpRequest) {
					PipeMonitor.get().get(sourceCtx.channel()).setRequest((DefaultHttpRequest) clientRequest);
				}
			}
		});
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}
}
