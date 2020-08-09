package org.hum.wiretiger.core.handler.http;

import org.hum.wiretiger.core.external.pipe_monitor.PipeMonitor;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
				// HTTP协议与对端建立连接
//				PipeMonitor.get().get(sourceCtx.channel()).recordStatus(PipeStatus.Connected);
				proxy2RemoteFuture.channel().writeAndFlush(clientRequest);
				if (clientRequest instanceof DefaultHttpRequest) {
					PipeMonitor.get().get(sourceCtx.channel()).setRequest((DefaultHttpRequest) clientRequest);
				} else {
					log.warn("found unknown request-type=" + clientRequest.getClass().getName() + ", instance=" + clientRequest);
				}
				// HTTP协议转发了请求
//				PipeMonitor.get().get(sourceCtx.channel()).recordStatus(PipeStatus.Forward);
			}
		});
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}
}
