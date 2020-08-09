package org.hum.wiretiger.core.handler.pipe;

import org.hum.wiretiger.core.external.pipe_monitor.PipeMonitor;
import org.hum.wiretiger.core.external.pipe_monitor.PipeStatus;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BackPipeHandler extends ChannelDuplexHandler {
	
	private Channel client2ProxyChannel;
	
	public BackPipeHandler(Channel client2ProxyChannel) {
		this.client2ProxyChannel = client2ProxyChannel;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		PipeMonitor.get().get(client2ProxyChannel).recordStatus(PipeStatus.Connected);
		ctx.fireChannelActive();
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		PipeMonitor.get().get(client2ProxyChannel).recordStatus(PipeStatus.Received);
        ctx.fireChannelRead(msg);
    }

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		PipeMonitor.get().get(client2ProxyChannel).inactive(ctx);
		ctx.fireChannelInactive();
	}
	

	@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	PipeMonitor.get().get(client2ProxyChannel).recordStatus(PipeStatus.Forward);
    	ctx.writeAndFlush(msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	log.error("Connection exception caught..", cause);
		PipeMonitor.get().get(client2ProxyChannel).error(ctx);
        ctx.fireExceptionCaught(cause);
    }
}
