package org.hum.wiretiger.ws.handler;

import org.hum.wiretiger.ws.bean.WsServerMessage;

import com.alibaba.fastjson.JSON;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketEncoder extends SimpleChannelInboundHandler<WsServerMessage> {
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WsServerMessage msg) throws Exception {
		ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
	}
}
