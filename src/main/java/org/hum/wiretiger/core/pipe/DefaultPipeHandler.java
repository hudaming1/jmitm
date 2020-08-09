package org.hum.wiretiger.core.pipe;

import org.hum.wiretiger.common.enumtype.Protocol;
import org.hum.wiretiger.core.handler.Forward;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.pipe.enumtype.PipeStatus;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class DefaultPipeHandler extends AbstractPipeHandler {
	
	public DefaultPipeHandler(PipeHolder pipeHolder, String host, int port) {
		super(pipeHolder);
		try {
			new Forward(this, host, port, pipeHolder.getProtocol() == Protocol.HTTPS).start().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void channelActive4Server(ChannelHandlerContext ctx) throws Exception {
		pipeHolder.registServer(ctx.channel());
		pipeHolder.recordStatus(PipeStatus.Connected);
	}

	@Override
	public void channelRead4Client(ChannelHandlerContext ctx, Object msg) throws Exception {
		pipeHolder.getServerChannel().writeAndFlush(msg);
		pipeHolder.recordStatus(PipeStatus.Read);
	}

	@Override
	public void channelRead4Server(ChannelHandlerContext ctx, Object msg) throws Exception {
		pipeHolder.getClientChannel().writeAndFlush(msg);
		pipeHolder.recordStatus(PipeStatus.Received);
	}

	@Override
	public void channelWrite4Client(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		pipeHolder.recordStatus(PipeStatus.Flushed);
	}

	@Override
	public void channelWrite4Server(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		pipeHolder.recordStatus(PipeStatus.Forward);
	}

	@Override
	public void channelInactive4Client(ChannelHandlerContext ctx) throws Exception {
		if (pipeHolder.getServerChannel().isActive()) {
			pipeHolder.getServerChannel().disconnect();
		}
		pipeHolder.recordStatus(PipeStatus.Closed);
	}

	@Override
	public void channelInactive4Server(ChannelHandlerContext ctx) throws Exception {
		if (pipeHolder.getClientChannel().isActive()) {
			pipeHolder.getClientChannel().disconnect();
		}
		pipeHolder.recordStatus(PipeStatus.Closed);
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
