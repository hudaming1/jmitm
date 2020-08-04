package org.hum.wiretiger.core.handler.http;

import org.hum.wiretiger.core.external.pipe_monitor.PipeMonitor;
import org.hum.wiretiger.core.external.pipe_monitor.PipeStatus;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * remote -> local
 * @author hudaming
 */
public class ForwardHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
	private Channel client2ProxyChannel;
	
	public ForwardHandler(Channel client2ProxyCtx) {
		this.client2ProxyChannel = client2ProxyCtx;
	}
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
        	ctx.channel().close();
        }
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse resp) throws Exception {
		
		
		PipeMonitor.get().get(client2ProxyChannel).addResponse(resp);
		PipeMonitor.get().get(client2ProxyChannel).recordStatus(PipeStatus.Received);
		
		// forward response
    	if (client2ProxyChannel.isActive()) {
    		this.client2ProxyChannel.writeAndFlush(resp).addListener(new GenericFutureListener<Future<? super Void>>() {
				@Override
				public void operationComplete(Future<? super Void> future) throws Exception {
					PipeMonitor.get().get(client2ProxyChannel).recordStatus(PipeStatus.Flushed);
				}
			});
    	}
	}
}