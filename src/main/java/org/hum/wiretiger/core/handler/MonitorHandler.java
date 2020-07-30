package org.hum.wiretiger.core.handler;

import org.hum.wiretiger.core.external.conmonitor.ConnectMonitor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MonitorHandler extends ChannelInboundHandlerAdapter {
	
	private ConnectMonitor connectMonitor = ConnectMonitor.get();

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		connectMonitor.add(host, port);
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelInactive();
	}
}
