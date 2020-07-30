package org.hum.wiretiger.core.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InactiveRelHandler extends ChannelInboundHandlerAdapter {

	private Channel needNoticeCloseChannel;
	
	public InactiveRelHandler(Channel channel) {
		this.needNoticeCloseChannel = channel;
	}
	
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        if (needNoticeCloseChannel.isActive()) {
        	needNoticeCloseChannel.close();
        }
    }
}