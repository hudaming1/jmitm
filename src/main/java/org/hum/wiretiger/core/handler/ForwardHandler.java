package org.hum.wiretiger.core.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

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
    	// forward response
    	if (channel.isActive()) {
    		this.channel.writeAndFlush(msg);
    	}
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
        	ctx.channel().close();
        }
    }
}