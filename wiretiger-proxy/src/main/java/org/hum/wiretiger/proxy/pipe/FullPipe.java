package org.hum.wiretiger.proxy.pipe;

import java.util.Stack;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeHolder;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;
import org.hum.wiretiger.proxy.session.WtSessionManager;
import org.hum.wiretiger.proxy.session.bean.WtSession;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class FullPipe extends AbstractPipeHandler {
	
	private EventHandler eventHandler;
	private WtPipeHolder pipeHolder;
	/**
	 * 保存了当前HTTP连接，没有等待响应的请求
	 */
	private Stack<WtSession> reqStack4WattingResponse = new Stack<>();

	public FullPipe(FrontPipe front, BackPipe back, EventHandler eventHandler, WtPipeHolder pipeHolder) {
		super(front, back);
		this.eventHandler = eventHandler;
		this.pipeHolder = pipeHolder;
		pipeHolder.recordStatus(PipeStatus.Parsed);
		pipeHolder.addEvent(PipeEventType.Parsed, "解析连接协议");
	}

	@Override
	public void channelActive4Server(ChannelHandlerContext ctx) throws Exception {
		pipeHolder.registServer(ctx.channel());
		pipeHolder.recordStatus(PipeStatus.Connected);
		pipeHolder.addEvent(PipeEventType.ServerConnected, "连接服务端(" + back.getHost() + ":" + back.getPort() + ")");
	}

	@Override
	public void channelRead4Client(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			pipeHolder.addEvent(PipeEventType.Read, "读取客户端请求，DefaultHttpRequest");
			pipeHolder.appendRequest((HttpRequest) msg);
		} else if (msg instanceof LastHttpContent) {
			pipeHolder.addEvent(PipeEventType.Read, "读取客户端请求，LastHttpContent");
		} else if (msg instanceof DefaultHttpContent){
			log.info("[NOTICE] host=" + pipeHolder.getName() + "/" + pipeHolder.getUri());
			pipeHolder.addEvent(PipeEventType.Read, "读取客户端请求，DefaultHttpContent");
		} else {
			log.warn("need support more types, find type=" + msg.getClass());
		}
		super.back.getChannel().writeAndFlush(msg);
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
				log.warn(this + "---reqStack4WattingResponse.size error, size=" + reqStack4WattingResponse.size());
				super.front.getChannel().writeAndFlush(msg);
				return ;
			}
			WtSession session = reqStack4WattingResponse.pop();
			byte[] bytes = null;
			if (resp.content().readableBytes() > 0) {
				bytes = new byte[resp.content().readableBytes()];
				resp.content().duplicate().readBytes(bytes);
			}
			session.setResponse(resp, bytes, System.currentTimeMillis());
			WtSessionManager.get().add(session);
			
			// 目前是当服务端返回结果，具备构建一个完整当Session后才触发NewSession事件，后续需要将动作置前
			eventHandler.fireNewSessionEvent(pipeHolder, session);
		} else {
			log.warn("need support more types, find type=" + msg.getClass());
		}
		
		pipeHolder.recordStatus(PipeStatus.Received);
		eventHandler.fireReceiveEvent(pipeHolder);
		pipeHolder.appendResponse((FullHttpResponse) msg);
		super.front.getChannel().writeAndFlush(msg);
	}

	@Override
	public void channelWrite4Client(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		pipeHolder.recordStatus(PipeStatus.Flushed);
		pipeHolder.addEvent(PipeEventType.Flushed, "已将客户端请求转发给服务端");
		eventHandler.fireFlushEvent(pipeHolder);
	}

	@Override
	public void channelWrite4Server(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (msg instanceof HttpRequest) {
			reqStack4WattingResponse.push(new WtSession(pipeHolder.getId(), (HttpRequest) msg, System.currentTimeMillis()));
		}
		pipeHolder.recordStatus(PipeStatus.Forward);
		pipeHolder.addEvent(PipeEventType.Forward, "已将服务端响应转发给客户端");
		eventHandler.fireForwardEvent(pipeHolder);
	}

	@Override
	public void channelInactive4Client(ChannelHandlerContext ctx) throws Exception {
		if (super.back.getChannel().isActive()) {
			super.back.getChannel().close();
		}
		pipeHolder.recordStatus(PipeStatus.Closed);
		pipeHolder.addEvent(PipeEventType.ClientClosed, "客户端已经断开连接");
		log.info("client disconnect");
	}

	@Override
	public void channelInactive4Server(ChannelHandlerContext ctx) throws Exception {
		if (super.front.getChannel().isActive()) {
			super.front.getChannel().close();
		}
		pipeHolder.recordStatus(PipeStatus.Closed);
		pipeHolder.addEvent(PipeEventType.ServerClosed, "服务端已经断开连接");
		eventHandler.fireDisconnectEvent(pipeHolder);
		log.info("server disconnect");
	}

	@Override
	public void exceptionCaught4Client(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("front exception, pipeId=" + pipeHolder.getId(), cause);
		ctx.close();
		if (super.back.getChannel().isActive()) {
			super.back.getChannel().close();
		}
		pipeHolder.recordStatus(PipeStatus.Error);
		pipeHolder.addEvent(PipeEventType.Error, cause.getMessage());
	}

	@Override
	public void exceptionCaught4Server(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("back exception, pipeId=" + pipeHolder.getId(), cause);
		ctx.close();
		if (super.front.getChannel().isActive()) {
			super.front.getChannel().close();
		}
		pipeHolder.recordStatus(PipeStatus.Error);
		pipeHolder.addEvent(PipeEventType.Error, cause.getMessage());
		eventHandler.fireErrorEvent(pipeHolder);
	}

	public ChannelFuture connect() {
		return back.connect().addListener(f -> {
			back.getChannel().pipeline().addLast(FullPipe.this);
			if (pipeHolder.isHttps()) {
				back.handshakeFuture().addListener(new GenericFutureListener<Future<Channel>>() {
					@Override
					public void operationComplete(Future<Channel> future) throws Exception {
						if (future.isSuccess()) {
							pipeHolder.addEvent(PipeEventType.ServerTlsFinish, "服务端TLS握手完成");
						}
					}
				});
			}
		});
	}
	
	public void close() {
		if (front.getChannel() != null && front.getChannel().isActive()) {
			front.getChannel().close();
		}
		if (back.getChannel() != null && back.getChannel().isActive()) {
			back.getChannel().close();
		}
	}
}
