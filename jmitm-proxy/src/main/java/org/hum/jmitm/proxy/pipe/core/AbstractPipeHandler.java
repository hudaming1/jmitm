package org.hum.jmitm.proxy.pipe.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hum.jmitm.common.util.HttpMessageUtil.InetAddress;
import org.hum.jmitm.proxy.facade.PipeContext;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * 定义了管道的生命周期
 * @author hudaming
 */
@Slf4j
public abstract class AbstractPipeHandler extends ChannelDuplexHandler {

	protected Map<String, BackPipe> backMap;
	protected PipeContext wtContext;
	private final String BACK_PIPE_KEY = "%s:%s";
	
	public AbstractPipeHandler(PipeContext wtContext) {
		this.backMap = new ConcurrentHashMap<>();
		this.wtContext = wtContext;
	}
	
	protected BackPipe getBackChannel(Channel channel) {
		for (BackPipe back : backMap.values()) {
			if (back.getChannel() == channel) {
				return back;
			}
		}
		return null;
	}

	protected BackPipe initBackpipe(EventLoopGroup eventLoopGroup, InetAddress InetAddress) {
		BackPipe newBackpipe = initBackpipe0(eventLoopGroup, InetAddress);
		backMap.put(String.format(BACK_PIPE_KEY, InetAddress.getHost(), InetAddress.getPort()), newBackpipe);
		return newBackpipe;
	} 
	
	protected abstract BackPipe initBackpipe0(EventLoopGroup eventLoopGroup, InetAddress InetAddress);
	
	protected BackPipe select(String host, int port) {
		return backMap.get(String.format(BACK_PIPE_KEY, host, port));
	}
	
	protected void removeBackpipe(BackPipe back) {
		backMap.remove(String.format(BACK_PIPE_KEY, back.getHost(), back.getPort()));
	}
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	if (getBackChannel(ctx.channel()) != null) {
    		channelActive4Server(ctx);
    	} else if (ctx.channel() == wtContext.getClientChannel()) {
    		log.warn("[" + wtContext.getId() + "]front-channel active");
    	} else {
			log.warn("[" + wtContext.getId() + "]unknown channel type=" + ctx.channel());
    	}
    }

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (wtContext.getClientChannel() == ctx.channel()) {
			channelInactive4Client(ctx);
		} else if (getBackChannel(ctx.channel()) != null) {
			channelInactive4Server(ctx);
		} else {
			log.warn("[" + wtContext.getId() + "]unknown channel type=" + ctx.channel());
			ctx.fireChannelInactive();
		}
	} 

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (wtContext.getClientChannel() == ctx.channel()) {
			channelRead4Client(ctx, msg);
		} else if (getBackChannel(ctx.channel()) != null) {
			channelRead4Server(ctx, msg);
		} else {
			log.warn("[" + wtContext.getId() + "]unknown channel type=" + ctx.channel());
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (wtContext.getClientChannel() == ctx.channel()) {
			exceptionCaught4Client(ctx, cause);
		} else if (getBackChannel(ctx.channel()) != null) {
			exceptionCaught4Server(ctx, cause);
		} else {
			log.warn("[" + wtContext.getId() + "]unknown channel type=" + ctx.channel());
			ctx.fireExceptionCaught(cause);
		}
	}
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (wtContext.getClientChannel() == ctx.channel()) {
			channelWrite4Client(ctx, msg, promise);
		} else if (getBackChannel(ctx.channel()) != null) {
			channelWrite4Server(ctx, msg, promise);
		} else {
			for (BackPipe back : backMap.values()) {
				log.info("[" + wtContext.getId() + "]back=" + back.getChannel());
			}
			log.warn("[" + wtContext.getId() + "]unknown channel type=" + ctx.channel());
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
