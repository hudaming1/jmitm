package org.hum.wiretiger.core.handler.pipe;

import org.hum.wiretiger.core.external.pipe_monitor.Pipe;
import org.hum.wiretiger.core.external.pipe_monitor.PipeMonitor;
import org.hum.wiretiger.core.external.pipe_monitor.PipeStatus;
import org.hum.wiretiger.core.external.pipe_monitor.Protocol;
import org.hum.wiretiger.core.handler.bean.HttpRequest;
import org.hum.wiretiger.core.handler.helper.HttpHelper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FrontPipeHandler extends ChannelDuplexHandler {
	
	private static final String HTTPS_HANDSHAKE_METHOD = "CONNECT";
	private volatile boolean isParsed = false;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// init pipe
		PipeMonitor.get().add(new Pipe(ctx));
		ctx.fireChannelActive();
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    	if (isParsed) {
    		ctx.fireChannelRead(msg);
    		return ;
    	}
    	// 是否再需要价格parsing状态？
    	isParsed = true;
    	
    	HttpRequest request = HttpHelper.decode((ByteBuf) msg);
    	if (HTTPS_HANDSHAKE_METHOD.equalsIgnoreCase(request.getMethod())) {
    		PipeMonitor.get().get(ctx.channel()).setProtocol(Protocol.HTTPS.getCode());
    	} else {
    		PipeMonitor.get().get(ctx.channel()).setProtocol(Protocol.HTTP.getCode());
    	}
    	PipeMonitor.get().get(ctx.channel()).recordStatus(PipeStatus.Parsed);
        ctx.fireChannelRead(msg);
    }

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		PipeMonitor.get().get(ctx.channel()).inactive(ctx);
		ctx.fireChannelInactive();
	}
	

	@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	PipeMonitor.get().get(ctx.channel()).recordStatus(PipeStatus.Flushed);
    	ctx.writeAndFlush(msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	log.error("Connection exception caught..", cause);
		PipeMonitor.get().get(ctx.channel()).error(ctx);
        ctx.fireExceptionCaught(cause);
    }
}
