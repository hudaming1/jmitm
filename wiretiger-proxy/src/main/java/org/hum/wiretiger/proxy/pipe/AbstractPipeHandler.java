package org.hum.wiretiger.proxy.pipe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractPipeHandler extends ChannelDuplexHandler {

	protected FrontPipe front;
	protected Map<String, BackPipe> backMap;
	
	public AbstractPipeHandler(FrontPipe front, BackPipe back) {
		this.front = front;
		this.backMap = new ConcurrentHashMap<>();
		backMap.put(back.getHost() + ":" + back.getPort(), back);
	}
	
	private boolean isBackChannel(Channel channel) {
		for (BackPipe back : backMap.values()) {
			if (back.getChannel() == channel) {
				return true;
			}
		}
		return false;
	}
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	if (isBackChannel(ctx.channel())) {
    		channelActive4Server(ctx);
    	} else if (ctx.channel() == front.getChannel()) {
    		log.warn("front-channel active");
    	} else {
    		log.warn("unknow channel active");
    	}
    }

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (front.getChannel() == ctx.channel()) {
			channelInactive4Client(ctx);
		} else if (isBackChannel(ctx.channel())) {
			channelInactive4Server(ctx);
		} else {
			log.warn("unknown channel type");
			ctx.fireChannelInactive();
		}
	} 

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (front.getChannel() == ctx.channel()) {
			channelRead4Client(ctx, msg);
		} else if (isBackChannel(ctx.channel())) {
			channelRead4Server(ctx, msg);
		} else {
			log.warn("unknown channel type");
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (front.getChannel() == ctx.channel()) {
			exceptionCaught4Client(ctx, cause);
		} else if (isBackChannel(ctx.channel())) {
			exceptionCaught4Server(ctx, cause);
		} else {
			log.warn("unknown channel type");
			ctx.fireExceptionCaught(cause);
		}
	}
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (front.getChannel() == ctx.channel()) {
			channelWrite4Client(ctx, msg, promise);
		} else if (isBackChannel(ctx.channel())) {
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
