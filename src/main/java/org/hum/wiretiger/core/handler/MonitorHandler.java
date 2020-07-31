package org.hum.wiretiger.core.handler;

import org.hum.wiretiger.core.external.conmonitor.ConnectMonitor;
import org.hum.wiretiger.core.external.conmonitor.ConnectionStatus;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorHandler extends ChannelInboundHandlerAdapter {
	
	private ConnectMonitor connectMonitor = ConnectMonitor.get();

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().attr(AttributeKey.valueOf(ConnectionStatus.STATUS)).set(ConnectionStatus.Active);
		connectMonitor.add(ctx.channel());
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().attr(AttributeKey.valueOf(ConnectionStatus.STATUS)).set(ConnectionStatus.InActive);
		if (!connectMonitor.isExists(ctx.channel())) {
			log.info("found uncatched connection..");
			ctx.fireChannelInactive();
			return ;
		}
		connectMonitor.remove(ctx.channel());
		log.info("Connection removed");
		ctx.fireChannelInactive();
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	ctx.channel().attr(AttributeKey.valueOf(ConnectionStatus.STATUS)).set(ConnectionStatus.InActive);
		if (!connectMonitor.isExists(ctx.channel())) {
			log.info("found uncatched connection..");
			ctx.fireChannelInactive();
			return ;
		}
		connectMonitor.remove(ctx.channel());
		log.info("Connection exception caught..");
        ctx.fireExceptionCaught(cause);
    }
}
