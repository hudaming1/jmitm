package org.hum.jmitm.console.websocket.handler;

import org.hum.jmitm.console.websocket.bean.WsClientMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class BusinessServerHandler extends SimpleChannelInboundHandler<WsClientMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WsClientMessage msg) throws Exception {
		// log.info("read " + msg.toString() + " from client");
	}
}
