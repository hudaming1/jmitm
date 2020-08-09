package org.hum.wiretiger.core.pipe;

import org.hum.wiretiger.core.handler.http.Forward;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class DefaultPipeHandler extends AbstractPipeHandler {
	
	public DefaultPipeHandler(PipeHolder pipeHolder, String host, int port) {
		super(pipeHolder);
		try {
			new Forward(this, host, port).start().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void channelActive4Server(ChannelHandlerContext ctx) throws Exception {
		pipeHolder.registServer(ctx.channel());
	}

	@Override
	public void channelRead4Client(ChannelHandlerContext ctx, Object msg) throws Exception {
		pipeHolder.getServerChannel().writeAndFlush(msg);
	}

	@Override
	public void channelRead4Server(ChannelHandlerContext ctx, Object msg) throws Exception {
		pipeHolder.getClientChannel().writeAndFlush(msg);
	}

	@Override
	public void channelInactive4Client(ChannelHandlerContext ctx) throws Exception {
		if (pipeHolder.getServerChannel().isActive()) {
			pipeHolder.getServerChannel().disconnect();
		}
	}

	@Override
	public void channelInactive4Server(ChannelHandlerContext ctx) throws Exception {
		if (pipeHolder.getClientChannel().isActive()) {
			pipeHolder.getClientChannel().disconnect();
		}
	}

	@Override
	public void exceptionCaught4Client(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		if (pipeHolder.getClientChannel().isActive()) {
			pipeHolder.getClientChannel().disconnect();
		}
	}

	@Override
	public void exceptionCaught4Server(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		if (pipeHolder.getServerChannel().isActive()) {
			pipeHolder.getServerChannel().disconnect();
		}
	}

}
