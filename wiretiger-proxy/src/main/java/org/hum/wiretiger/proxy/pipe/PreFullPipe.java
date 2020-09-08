package org.hum.wiretiger.proxy.pipe;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PreFullPipe extends ChannelInboundHandlerAdapter {
	
	private EventHandler eventHandler;
	private WtPipeContext wtContext;
	
	public PreFullPipe(WtPipeContext wtContext, EventHandler eventHandler) {
		this.wtContext = wtContext;
		this.eventHandler = eventHandler;
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	log.info("[" + wtContext.getId() + "] client disconnect");
    	wtContext.recordStatus(PipeStatus.Closed);
    	wtContext.addEvent(PipeEventType.ClientClosed, "客户端断开连接(PreFullPipe)");
    	eventHandler.fireDisconnectEvent(wtContext);
        ctx.fireChannelInactive();
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	log.info("[{}] fire read_event", wtContext.getId());
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	log.error("[" + wtContext.getId() + "] client connection error", cause);
    	wtContext.recordStatus(PipeStatus.Error);
    	wtContext.addEvent(PipeEventType.Error, "客户端建立连接时发生异常," + cause.getMessage());
    	eventHandler.fireChangeEvent(wtContext);
        ctx.fireExceptionCaught(cause);
    }
}
