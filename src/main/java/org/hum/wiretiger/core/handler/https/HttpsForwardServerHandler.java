package org.hum.wiretiger.core.handler.https;

import org.hum.wiretiger.core.external.pipe_monitor.PipeMonitor;
import org.hum.wiretiger.core.handler.helper.HttpsClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

public class HttpsForwardServerHandler extends SimpleChannelInboundHandler<HttpObject> {

	private String host;
	private int port;

	public HttpsForwardServerHandler(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void channelRead0(ChannelHandlerContext client2ProxyCtx, HttpObject msg) throws Exception {
		if (msg instanceof DefaultHttpRequest) {
			PipeMonitor.get().get(client2ProxyCtx.channel()).setRequest((DefaultHttpRequest) msg);
			FullHttpResponse response = HttpsClient.send(host, port, (HttpRequest) msg);
			PipeMonitor.get().get(client2ProxyCtx.channel()).addResponse(response);
			client2ProxyCtx.writeAndFlush(response);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}
}
