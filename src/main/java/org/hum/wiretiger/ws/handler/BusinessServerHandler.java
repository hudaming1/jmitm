package org.hum.wiretiger.ws.handler;

import org.hum.wiretiger.ws.bean.WsClientMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessServerHandler extends SimpleChannelInboundHandler<WsClientMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WsClientMessage msg) throws Exception {
		log.info(msg.toString());
	}
}
