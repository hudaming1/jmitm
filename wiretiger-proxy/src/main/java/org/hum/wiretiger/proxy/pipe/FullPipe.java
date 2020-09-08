package org.hum.wiretiger.proxy.pipe;

import java.util.Stack;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
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
	private WtPipeContext wtContext;
	/**
	 * 保存了当前HTTP连接，没有等待响应的请求
	 */
	private Stack<WtSession> reqStack4WattingResponse = new Stack<>();

	public FullPipe(FrontPipe front, BackPipe back, EventHandler eventHandler, WtPipeContext wtContext) {
		super(front, back);
		this.eventHandler = eventHandler;
		this.wtContext = wtContext;
		wtContext.recordStatus(PipeStatus.Parsed);
		wtContext.addEvent(PipeEventType.Parsed, "解析连接协议, 准备连接目标服务器(" + back.getHost() + ":" + back.getPort() + ")");
		wtContext.setName(front.getHost() + ":" + front.getPort() + "->" + back.getHost() + ":" + back.getPort());
		eventHandler.fireChangeEvent(wtContext);
	}

	@Override
	public void channelActive4Server(ChannelHandlerContext ctx) throws Exception {
		wtContext.registServer(ctx.channel());
		wtContext.recordStatus(PipeStatus.Connected);
		wtContext.addEvent(PipeEventType.ServerConnected, "完成与目标服务器(" + back.getHost() + ":" + back.getPort() + ")建立连接");
	}

	@Override
	public void channelRead4Client(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			wtContext.addEvent(PipeEventType.Read, "读取客户端请求，DefaultHttpRequest");
			wtContext.appendRequest((HttpRequest) msg);
		} else if (msg instanceof LastHttpContent) {
			wtContext.addEvent(PipeEventType.Read, "读取客户端请求，LastHttpContent");
		} else {
			log.warn("need support more types, find type=" + msg.getClass());
		}
		super.back.getChannel().writeAndFlush(msg);
		wtContext.recordStatus(PipeStatus.Read);
		eventHandler.fireChangeEvent(wtContext);
	}

	/**
	 * 读取到对端服务器请求
	 */
	@Override
	public void channelRead4Server(ChannelHandlerContext ctx, Object msg) throws Exception {
		log.info("[" + wtContext.getId() + "] 6");
		if (msg instanceof FullHttpResponse) {
			FullHttpResponse resp = (FullHttpResponse) msg;
			wtContext.appendResponse(resp);
			wtContext.addEvent(PipeEventType.Received, "读取服务端请求，字节数\"" + resp.content().readableBytes() + "\"bytes");
			
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
			eventHandler.fireNewSessionEvent(wtContext, session);
		} else if (msg instanceof LastHttpContent) { 
			log.warn("need support more types, find type=" + msg.getClass());
		} else {
			wtContext.addEvent(PipeEventType.Received, "读取服务端请求(" + msg.getClass() + ")");
			log.warn("need support more types, find type=" + msg.getClass());
		}
		
		wtContext.recordStatus(PipeStatus.Received);
		eventHandler.fireChangeEvent(wtContext);
		super.front.getChannel().writeAndFlush(msg);
	}

	@Override
	public void channelWrite4Client(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		log.info("[" + wtContext.getId() + "] 7");
		wtContext.recordStatus(PipeStatus.Flushed);
		wtContext.addEvent(PipeEventType.Flushed, "已将服务端响应转发给客户端");
		eventHandler.fireChangeEvent(wtContext);
	}

	@Override
	public void channelWrite4Server(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (msg instanceof HttpRequest) {
			reqStack4WattingResponse.push(new WtSession(wtContext.getId(), (HttpRequest) msg, System.currentTimeMillis()));
		}
		// [HTTP] 5.ChannelHandler拦截写事件
		log.info("[" + wtContext.getId() + "] 5");
		wtContext.recordStatus(PipeStatus.Forward);
		wtContext.addEvent(PipeEventType.Forward, "已将客户端请求转发给服务端");
		eventHandler.fireChangeEvent(wtContext);
	}

	@Override
	public void channelInactive4Client(ChannelHandlerContext ctx) throws Exception {
		if (back.getChannel() != null && back.getChannel().isActive()) {
			back.getChannel().close();
		}
		wtContext.recordStatus(PipeStatus.Closed);
		wtContext.addEvent(PipeEventType.ClientClosed, "客户端已经断开连接");
		log.info("client disconnect");
	}

	@Override
	public void channelInactive4Server(ChannelHandlerContext ctx) throws Exception {
		if (front.getChannel() != null && front.getChannel().isActive()) {
			front.getChannel().close();
		}
		wtContext.recordStatus(PipeStatus.Closed);
		wtContext.addEvent(PipeEventType.ServerClosed, "服务端已经断开连接");
		eventHandler.fireDisconnectEvent(wtContext);
		log.info("server disconnect");
	}

	@Override
	public void exceptionCaught4Client(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("front exception, pipeId=" + wtContext.getId(), cause);
		wtContext.recordStatus(PipeStatus.Error);
		wtContext.addEvent(PipeEventType.Error, "客户端异常：" + cause.getMessage());
		close();
	}

	@Override
	public void exceptionCaught4Server(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("back exception, pipeId=" + wtContext.getId(), cause);
		wtContext.recordStatus(PipeStatus.Error);
		wtContext.addEvent(PipeEventType.Error, "服务端异常：" + cause.getMessage());
		eventHandler.fireErrorEvent(wtContext);
		close();
	}

	public ChannelFuture connect() {
		return back.connect().addListener(f -> {
			// [HTTP] 3.给back端挂上ChannelHandler，监管所有读写操作
			log.info("[" + wtContext.getId() + "] 3 " + f.isSuccess());
			if (!f.isSuccess()) {
				log.error("[" + wtContext.getId() + "] server connect error,", f);
				this.close();
				wtContext.recordStatus(PipeStatus.Error);
				wtContext.addEvent(PipeEventType.Error, "与目标服务器建立连接失败：" + f.cause().getMessage());
				eventHandler.fireErrorEvent(wtContext);
				return ;
			}
			// Pipe在connect后才添加上，导致事件丢失
			back.getChannel().pipeline().addLast(FullPipe.this);
			if (wtContext.isHttps()) {
				back.handshakeFuture().addListener(new GenericFutureListener<Future<Channel>>() {
					@Override
					public void operationComplete(Future<Channel> future) throws Exception {
						if (!future.isSuccess()) {
							log.error("[" + wtContext.getId() + "] server tls error,", future);
							wtContext.recordStatus(PipeStatus.Closed);
							wtContext.addEvent(PipeEventType.ServerClosed, "服务端TLS握手失败：" + future.cause().getMessage());
							eventHandler.fireChangeEvent(wtContext);
							return ;
						}
						wtContext.addEvent(PipeEventType.ServerTlsFinish, "服务端TLS握手完成");
						eventHandler.fireChangeEvent(wtContext);
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
		// 确保最终状态是closed
		wtContext.recordStatus(PipeStatus.Closed);
	}
}
