package org.hum.wiretiger.console.websocket.handler;

import org.hum.wiretiger.console.websocket.bean.WsClientMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessServerHandler extends SimpleChannelInboundHandler<WsClientMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WsClientMessage msg) throws Exception {
		// log.info("read " + msg.toString() + " from client");
	}
}
