package org.hum.wiretiger.core.pipe;

import java.util.Stack;

import org.hum.wiretiger.common.enumtype.Protocol;
import org.hum.wiretiger.common.exception.WiretigerException;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.core.pipe.enumtype.PipeStatus;
import org.hum.wiretiger.core.pipe.event.EventHandler;
import org.hum.wiretiger.core.session.SessionManager;
import org.hum.wiretiger.core.session.bean.WtSession;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class DefaultPipeHandler extends AbstractPipeHandler {
	
	private final SessionManager cm = SessionManager.get();
	/**
	 * 保存了当前HTTP连接，没有等待响应的请求
	 */
	private Stack<WtSession> reqStack4WattingResponse = new Stack<>();
	private EventHandler eventHandler;
	
	public DefaultPipeHandler(EventHandler eventHandler, PipeHolder pipeHolder, String host, int port) {
		super(pipeHolder);
		this.eventHandler = eventHandler;
		try {
			new HttpOrHttpsForward(this, host, port, pipeHolder.getProtocol() == Protocol.HTTPS).start().sync();
		} catch (InterruptedException e) {
			throw new WiretigerException("build connection failed", e);
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
		if (msg instanceof DefaultHttpRequest) {
			pipeHolder.addEvent(PipeEventType.Read, "读取客户端请求，DefaultHttpRequest");
			pipeHolder.appendRequest((DefaultHttpRequest) msg);
			reqStack4WattingResponse.push(new WtSession(pipeHolder.getId(), (DefaultHttpRequest) msg, System.currentTimeMillis()));
		} else if (msg instanceof LastHttpContent) {
			pipeHolder.addEvent(PipeEventType.Read, "读取客户端请求，LastHttpContent");
		} else {
			log.warn("need support more types, find type=" + msg.getClass());
		}
		pipeHolder.getServerChannel().writeAndFlush(msg);
		pipeHolder.recordStatus(PipeStatus.Read);
		eventHandler.fireReadEvent(pipeHolder);
	}

	/**
	 * 读取到对端服务器请求
	 */
	@Override
	public void channelRead4Server(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpResponse) {
			FullHttpResponse resp = (FullHttpResponse) msg;
			pipeHolder.addEvent(PipeEventType.Received, "读取服务端请求，字节数\"" + resp.content().readableBytes() + "\"bytes");
			if (reqStack4WattingResponse.isEmpty() || reqStack4WattingResponse.size() > 1) {
				log.warn("reqStack4WattingResponse.size error, size=" + reqStack4WattingResponse.size());
			}
			WtSession session = reqStack4WattingResponse.pop();
			byte[] bytes = null;
			if (resp.content().readableBytes() > 0) {
				bytes = new byte[resp.content().readableBytes()];
				resp.content().duplicate().readBytes(bytes);
			}
			session.setResponse(resp, bytes, System.currentTimeMillis());
			cm.add(session);
			eventHandler.fireNewSessionEvent(pipeHolder, session);
		} else {
			log.warn("need support more types, find type=" + msg.getClass());
		}
		pipeHolder.getClientChannel().writeAndFlush(msg);
		pipeHolder.appendResponse((FullHttpResponse) msg);
		pipeHolder.recordStatus(PipeStatus.Received);
		eventHandler.fireReceiveEvent(pipeHolder);
	}

	@Override
	public void channelWrite4Client(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		pipeHolder.recordStatus(PipeStatus.Flushed);
		pipeHolder.addEvent(PipeEventType.Flushed, "已将客户端请求转发给服务端");
		eventHandler.fireFlushEvent(pipeHolder);
	}

	@Override
	public void channelWrite4Server(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		pipeHolder.recordStatus(PipeStatus.Forward);
		pipeHolder.addEvent(PipeEventType.Forward, "已将服务端响应转发给客户端");
		eventHandler.fireForwardEvent(pipeHolder);
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
		eventHandler.fireDisconnectEvent(pipeHolder);
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
		eventHandler.fireErrorEvent(pipeHolder);
	}
}
