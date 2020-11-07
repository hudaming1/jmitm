package org.hum.wiredog.console.websocket.handler;

import org.hum.wiredog.console.websocket.bean.WsServerMessage;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketEncoder extends MessageToByteEncoder<WsServerMessage<?>> {
	
	@Override
	protected void encode(ChannelHandlerContext ctx, WsServerMessage<?> msg, ByteBuf out) throws Exception {
		ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
	}
}
