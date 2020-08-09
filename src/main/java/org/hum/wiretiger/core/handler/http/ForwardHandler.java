package org.hum.wiretiger.core.handler.http;

import org.hum.wiretiger.core.pipe.bean.PipeHolder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * remote -> local
 * @author hudaming
 */
public class ForwardHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
	private PipeHolder pipeHolder;
	
	public ForwardHandler(PipeHolder pipeHolder) {
		this.pipeHolder = pipeHolder;
	}
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
        	ctx.channel().close();
        }
        ctx.fireExceptionCaught(cause);
        pipeHolder.onError4BackChannel();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse resp) throws Exception {
        pipeHolder.onReadResponse(resp);
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        pipeHolder.onDisconnect4BackChannel();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        pipeHolder.onConnect4BackChannel(ctx.channel());
    }
}