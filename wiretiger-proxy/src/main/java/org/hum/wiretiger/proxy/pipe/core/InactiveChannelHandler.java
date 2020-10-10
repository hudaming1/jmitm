package org.hum.wiretiger.proxy.pipe.core;

import org.hum.wiretiger.proxy.facade.PipeInvokeChain;
import org.hum.wiretiger.proxy.facade.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InactiveChannelHandler extends ChannelInboundHandlerAdapter {
	
	private PipeInvokeChain fullPipeHandler;
	private WtPipeContext wtContext;
	
	public InactiveChannelHandler(WtPipeContext wtContext, PipeInvokeChain fullPipeHandler) {
		this.wtContext = wtContext;
		this.fullPipeHandler = fullPipeHandler;
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	wtContext.recordStatus(PipeStatus.Closed);
    	wtContext.addEvent(PipeEventType.ClientClosed, "客户端提前断开连接(InactiveChannelHandler)");
    	fullPipeHandler.clientClose(wtContext);
    	if (ctx.channel().isActive()) {
    		ctx.fireChannelInactive();
    	}
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (ctx.channel().isActive()) {
    		ctx.fireChannelRead(msg);
    	}
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	log.error("[" + wtContext.getId() + "] client connection error, cause=" + cause.getMessage());
    	wtContext.recordStatus(PipeStatus.Error);
    	wtContext.addEvent(PipeEventType.Error, "客户端建立连接时发生异常," + cause.getMessage());
    	fullPipeHandler.clientError(wtContext, cause);
    	ctx.close();
    }
}
