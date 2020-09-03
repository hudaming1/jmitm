package org.hum.wiretiger.proxy.pipe.v2;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeHolder;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class FullPipe extends AbstractPipeHandlerNew {
	
	private EventHandler eventHandler;
	private WtPipeHolder pipeHolder;

	public FullPipe(FrontPipe front, BackPipe back, EventHandler eventHandler, WtPipeHolder pipeHolder) {
		super(front, back);
		this.eventHandler = eventHandler;
		this.pipeHolder = pipeHolder;
		this.front.getChannel().pipeline().addLast(this);
	}

	public ChannelFuture connect() {
		try {
			ChannelFuture backConnectFuture = back.connect();
			back.getChannel().pipeline().addLast(FullPipe.this);
			return backConnectFuture;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void channelActive4Server(ChannelHandlerContext ctx) throws Exception {
		log.info("connect server");
	}

	@Override
	public void channelRead4Client(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.back.getChannel().writeAndFlush(msg);
//		log.info("channelRead4Client, flush to server, msg=" + msg.getClass());
	}

	/**
	 * 读取到对端服务器请求
	 */
	@Override
	public void channelRead4Server(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.front.getChannel().writeAndFlush(msg);
//		log.info("channelRead4Server, flush to client, msg=" + msg.getClass());
//		if (msg instanceof HttpResponse) {
//			HttpResponse resp = (HttpResponse) msg;
//			if (resp.decoderResult().isFinished()) {
//				super.front.getChannel().close();
//				System.out.println("closed");
//			}
//		}
	}

	@Override
	public void channelWrite4Client(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//		pipeHolder.recordStatus(PipeStatus.Flushed);
//		pipeHolder.addEvent(PipeEventType.Flushed, "已将客户端请求转发给服务端");
//		eventHandler.fireFlushEvent(pipeHolder);
	}

	@Override
	public void channelWrite4Server(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//		pipeHolder.recordStatus(PipeStatus.Forward);
//		pipeHolder.addEvent(PipeEventType.Forward, "已将服务端响应转发给客户端");
//		eventHandler.fireForwardEvent(pipeHolder);
	}

	@Override
	public void channelInactive4Client(ChannelHandlerContext ctx) throws Exception {
		if (super.back.getChannel().isActive()) {
			super.back.getChannel().disconnect();
		}
//		pipeHolder.recordStatus(PipeStatus.Closed);
//		pipeHolder.addEvent(PipeEventType.ClientClosed, "客户端已经断开连接");
//		log.info("client disconnect");
	}

	@Override
	public void channelInactive4Server(ChannelHandlerContext ctx) throws Exception {
		if (super.front.getChannel().isActive()) {
			super.front.getChannel().disconnect();
		}
//		pipeHolder.recordStatus(PipeStatus.Closed);
//		pipeHolder.addEvent(PipeEventType.ServerClosed, "服务端已经断开连接");
//		eventHandler.fireDisconnectEvent(pipeHolder);
//		log.info("server disconnect");
	}

	@Override
	public void exceptionCaught4Client(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		if (super.back.getChannel().isActive()) {
			super.back.getChannel().disconnect();
		}
//		pipeHolder.recordStatus(PipeStatus.Error);
	}

	@Override
	public void exceptionCaught4Server(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		if (super.front.getChannel().isActive()) {
			super.front.getChannel().disconnect();
		}
//		pipeHolder.recordStatus(PipeStatus.Error);
//		eventHandler.fireErrorEvent(pipeHolder);
	}
}
