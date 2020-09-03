package org.hum.wiretiger.proxy.pipe;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeHolder;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
			// TODO 与预期行为不一致
			backConnectFuture.channel().pipeline().addLast(this);
			System.out.println("22222");
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
		System.out.println("client read");
		super.back.getChannel().writeAndFlush(msg);
	}

	/**
	 * 读取到对端服务器请求
	 */
	@Override
	public void channelRead4Server(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("server read");
		super.front.getChannel().writeAndFlush(msg);
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
