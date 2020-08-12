package org.hum.wiretiger.core.pipe;

import java.util.Stack;

import org.hum.wiretiger.common.enumtype.Protocol;
import org.hum.wiretiger.common.exception.WireTigerException;
import org.hum.wiretiger.console.helper.HttpMessageUtil;
import org.hum.wiretiger.core.handler.Forward;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.core.pipe.enumtype.PipeStatus;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class DefaultPipeHandler extends AbstractPipeHandler {
	
	private Stack<HttpRequest> reqStack4WattingResponse = new Stack<>();
	
	public DefaultPipeHandler(PipeHolder pipeHolder, String host, int port) {
		super(pipeHolder);
		try {
			new Forward(this, host, port, pipeHolder.getProtocol() == Protocol.HTTPS).start().sync();
		} catch (InterruptedException e) {
			throw new WireTigerException("build connection failed", e);
		}
	}

	@Override
	public void channelActive4Server(ChannelHandlerContext ctx) throws Exception {
		pipeHolder.registServer(ctx.channel());
		pipeHolder.recordStatus(PipeStatus.Connected);
		pipeHolder.addEvent(PipeEventType.ServerConnected, "连接服务端");
	}

	@Override
	public void channelRead4Client(ChannelHandlerContext ctx, Object msg) throws Exception {
//		pipeHolder.getServerChannel().writeAndFlush(msg);
//		if (msg instanceof DefaultHttpRequest) {
//			pipeHolder.appendRequest((DefaultHttpRequest) msg);
//		}
//		pipeHolder.recordStatus(PipeStatus.Read);
		
		if (msg instanceof DefaultHttpRequest) {
			DefaultHttpRequest req = (DefaultHttpRequest) msg;
//			HttpMessageUtil.appendRequest(new StringBuilder(), req).toString().replaceAll(StringUtil.NEWLINE, "<br />")
			pipeHolder.addEvent(PipeEventType.Read, "读取客户端请求，DefaultHttpRequest");
			pipeHolder.appendRequest((DefaultHttpRequest) msg);
			reqStack4WattingResponse.push((DefaultHttpRequest) msg);
		} else if (msg instanceof LastHttpContent) {
			pipeHolder.addEvent(PipeEventType.Read, "读取客户端请求，LastHttpContent");
		}
		pipeHolder.getServerChannel().writeAndFlush(msg);
		pipeHolder.recordStatus(PipeStatus.Read);
	}

	/**
	 * 读取到对端服务器请求
	 */
	@Override
	public void channelRead4Server(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpResponse) {
			FullHttpResponse resp = (FullHttpResponse) msg;
			pipeHolder.addEvent(PipeEventType.Received, "读取服务端请求，字节数\"" + resp.content().readableBytes() + "\"bytes");
		} else {
			log.warn("need support more types, find type=" + msg.getClass());
		}
		pipeHolder.getClientChannel().writeAndFlush(msg);
		pipeHolder.appendResponse((FullHttpResponse) msg);
		pipeHolder.recordStatus(PipeStatus.Received);
	}

	@Override
	public void channelWrite4Client(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		pipeHolder.recordStatus(PipeStatus.Flushed);
		pipeHolder.addEvent(PipeEventType.Flushed, "已将客户端请求转发给服务端");
	}

	@Override
	public void channelWrite4Server(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		pipeHolder.recordStatus(PipeStatus.Forward);
		pipeHolder.addEvent(PipeEventType.Forward, "已将服务端响应转发给客户端");
	}

	@Override
	public void channelInactive4Client(ChannelHandlerContext ctx) throws Exception {
		if (pipeHolder.getServerChannel().isActive()) {
			pipeHolder.getServerChannel().disconnect();
		}
		pipeHolder.recordStatus(PipeStatus.Closed);
		pipeHolder.addEvent(PipeEventType.ClientClosed, "客户端已经断开连接");
	}

	@Override
	public void channelInactive4Server(ChannelHandlerContext ctx) throws Exception {
		if (pipeHolder.getClientChannel().isActive()) {
			pipeHolder.getClientChannel().disconnect();
		}
		pipeHolder.recordStatus(PipeStatus.Closed);
		pipeHolder.addEvent(PipeEventType.ServerClosed, "服务端已经断开连接");
	}

	@Override
	public void exceptionCaught4Client(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		if (pipeHolder.getClientChannel().isActive()) {
			pipeHolder.getClientChannel().disconnect();
		}
		pipeHolder.recordStatus(PipeStatus.Error);
	}

	@Override
	public void exceptionCaught4Server(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		if (pipeHolder.getServerChannel().isActive()) {
			pipeHolder.getServerChannel().disconnect();
		}
		pipeHolder.recordStatus(PipeStatus.Error);
	}
}
