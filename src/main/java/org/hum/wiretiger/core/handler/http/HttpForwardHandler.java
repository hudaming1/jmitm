package org.hum.wiretiger.core.handler.http;

import org.hum.wiretiger.core.external.conmonitor.ConnectionStatus;
import org.hum.wiretiger.core.handler.bean.Pipe;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpRequest;
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
	public void channelRead0(ChannelHandlerContext sourceCtx, HttpObject clientRequest) throws Exception {
		// 建立远端转发连接（远端收到响应后，一律转发给本地）
		new Forward(sourceCtx, host, port).start().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture proxy2RemoteFuture) throws Exception {
				((Pipe) sourceCtx.channel().attr(AttributeKey.valueOf(Pipe.PIPE_ATTR_NAME)).get()).setStatus(ConnectionStatus.Connected);
				// forward request
				proxy2RemoteFuture.channel().writeAndFlush(clientRequest);
				((Pipe) sourceCtx.channel().attr(AttributeKey.valueOf(Pipe.PIPE_ATTR_NAME)).get()).setStatus(ConnectionStatus.Forward);
				if (clientRequest instanceof DefaultHttpRequest) {
					((Pipe) sourceCtx.channel().attr(AttributeKey.valueOf(Pipe.PIPE_ATTR_NAME)).get()).setRequest((DefaultHttpRequest) clientRequest);
				}
			}
		});
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
