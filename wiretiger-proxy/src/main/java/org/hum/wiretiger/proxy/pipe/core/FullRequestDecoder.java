package org.hum.wiretiger.proxy.pipe.core;

import org.hum.wiretiger.proxy.util.HttpMessageUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;

public class FullRequestDecoder extends ChannelInboundHandlerAdapter {

	private HttpRequest request;
	// 大部分Request都没有Body
	private ByteBuf content;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (msg instanceof HttpRequest) {
			this.request = (HttpRequest) msg;
			// 将URI改为相对路径（全路径URI个别服务器无法解析提示404）
			this.request.setUri(HttpMessageUtil.parse2RelativeURI(this.request.uri()));
			content = Unpooled.buffer(0).retain();
		}
    	if (msg instanceof HttpContent) {
    		HttpContent body = (HttpContent) msg;
    		if (body.content().readableBytes() > 0) {
    			content.writeBytes(body.content().duplicate());
    		}
    	} 
    	if (msg instanceof LastHttpContent) {
			LastHttpContent requestBody = (LastHttpContent) msg;
			DefaultFullHttpRequest fullRequest = new DefaultFullHttpRequest(request.protocolVersion(), request.method(), request.uri(), content, request.headers(), requestBody.trailingHeaders());
			ctx.fireChannelRead(fullRequest);
		} 
    }
}
