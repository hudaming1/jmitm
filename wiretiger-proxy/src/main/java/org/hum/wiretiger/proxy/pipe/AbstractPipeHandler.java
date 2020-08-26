package org.hum.wiretiger.proxy.pipe;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeHolder;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractPipeHandler extends ChannelDuplexHandler {
	
	protected WtPipeHolder pipeHolder;
	
	public AbstractPipeHandler(WtPipeHolder pipeHolder) {
		this.pipeHolder = pipeHolder;
	}
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	channelActive4Server(ctx);
    }

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (pipeHolder.getClientChannel() == ctx.channel()) {
			channelInactive4Client(ctx);
		} else if (pipeHolder.getServerChannel() == ctx.channel()) {
			channelInactive4Server(ctx);
		} else {
			log.warn("unknown channel type");
			ctx.fireChannelInactive();
		}
	} 

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (pipeHolder.getClientChannel() == ctx.channel()) {
			channelRead4Client(ctx, msg);
		} else if (pipeHolder.getServerChannel() == ctx.channel()) {
			channelRead4Server(ctx, msg);
		} else {
			log.warn("unknown channel type");
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (pipeHolder.getClientChannel() == ctx.channel()) {
			exceptionCaught4Client(ctx, cause);
		} else if (pipeHolder.getServerChannel() == ctx.channel()) {
			exceptionCaught4Server(ctx, cause);
		} else {
			log.warn("unknown channel type");
			ctx.fireExceptionCaught(cause);
		}
	}
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (pipeHolder.getClientChannel() == ctx.channel()) {
			channelWrite4Client(ctx, msg, promise);
		} else if (pipeHolder.getServerChannel() == ctx.channel()) {
			channelWrite4Server(ctx, msg, promise);
		} else {
			log.warn("unknown channel type");
		}
		ctx.write(msg, promise);
	}
	
	public abstract void channelActive4Server(ChannelHandlerContext ctx) throws Exception;
	
	public abstract void channelRead4Client(ChannelHandlerContext ctx, Object msg) throws Exception;
	
	public abstract void channelRead4Server(ChannelHandlerContext ctx, Object msg) throws Exception;
	
	public abstract void channelWrite4Client(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception;
	
	public abstract void channelWrite4Server(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception;
	
	public abstract void channelInactive4Client(ChannelHandlerContext ctx) throws Exception;
	
	public abstract void channelInactive4Server(ChannelHandlerContext ctx) throws Exception;
	
	public abstract void exceptionCaught4Client(ChannelHandlerContext ctx, Throwable cause) throws Exception;
	
	public abstract void exceptionCaught4Server(ChannelHandlerContext ctx, Throwable cause) throws Exception;
}
