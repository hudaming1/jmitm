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
	private Channel channel;
	@SuppressWarnings("unused")
	private String name;
	
	public ForwardHandler(Channel channel) {
		this.channel = channel;
	}
	
	public ForwardHandler(String name, Channel channel) {
		this.channel = channel;
		this.name = name;
	}
	
    @Override
    public void channelRead(ChannelHandlerContext remoteCtx, Object msg) throws Exception {
    	System.out.println(msg);
    	// forward response
    	if (channel.isActive()) {
    		this.channel.writeAndFlush(msg).addListener(new GenericFutureListener<Future<? super Void>>() {
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