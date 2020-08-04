package org.hum.wiretiger.core.handler;

import org.hum.wiretiger.core.external.conmonitor.PipeMonitor;
import org.hum.wiretiger.core.external.conmonitor.ConnectionStatus;
import org.hum.wiretiger.core.handler.bean.Pipe;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorHandler extends ChannelInboundHandlerAdapter {
	
	private PipeMonitor connectionMonitor = PipeMonitor.get();

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// init pipe
		Pipe newPipe = new Pipe();
		newPipe.setSourceCtx(ctx);
		newPipe.setStatus(ConnectionStatus.Active);
		ctx.channel().attr(AttributeKey.valueOf(Pipe.PIPE_ATTR_NAME)).set(newPipe);
		connectionMonitor.add(newPipe);
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		((Pipe) ctx.channel().attr(AttributeKey.valueOf(Pipe.PIPE_ATTR_NAME)).get()).setStatus(ConnectionStatus.InActive);
		ctx.fireChannelInactive();
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
		((Pipe) ctx.channel().attr(AttributeKey.valueOf(Pipe.PIPE_ATTR_NAME)).get()).setStatus(ConnectionStatus.Error);
		log.error("Connection exception caught..", cause);
        ctx.fireExceptionCaught(cause);
    }
}
