package org.hum.wiretiger.core.handler.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * remote -> local
 * @author hudaming
 */
public class ForwardHandler extends ChannelInboundHandlerAdapter {
	private Channel remove2Local;
	@SuppressWarnings("unused")
	private String name;
	
	public ForwardHandler(Channel channel) {
		this.remove2Local = channel;
	}
	
	public ForwardHandler(String name, Channel channel) {
		this.remove2Local = channel;
		this.name = name;
	}
	
    @Override
    public void channelRead(ChannelHandlerContext remoteCtx, Object resp) throws Exception {
    	// forward response
    	if (remove2Local.isActive()) {
    		this.remove2Local.writeAndFlush(resp).addListener(new GenericFutureListener<Future<? super Void>>() {
				@Override
				public void operationComplete(Future<? super Void> future) throws Exception {
					// TODO close
				}
			});
    	}
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
        	ctx.channel().close();
        }
    }
}