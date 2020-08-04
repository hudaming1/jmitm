package org.hum.wiretiger.core.handler;

import org.hum.wiretiger.core.external.pipe_monitor.Pipe;
import org.hum.wiretiger.core.external.pipe_monitor.PipeMonitor;
import org.hum.wiretiger.core.external.pipe_monitor.PipeStatus;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorHandler extends ChannelInboundHandlerAdapter {
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// init pipe
		Pipe newPipe = new Pipe();
		newPipe.setSourceCtx(ctx);
		newPipe.recordStatus(PipeStatus.Init);
		PipeMonitor.get().add(newPipe);
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		PipeMonitor.get().get(ctx.channel()).recordStatus(PipeStatus.Closed);
		ctx.fireChannelInactive();
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
		PipeMonitor.get().get(ctx.channel()).recordStatus(PipeStatus.Error);
		log.error("Connection exception caught..", cause);
        ctx.fireExceptionCaught(cause);
    }
}
