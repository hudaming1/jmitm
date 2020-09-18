package org.hum.wiretiger.proxy.pipe;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;

public class FullRequestDecoder extends ChannelInboundHandlerAdapter {

	private HttpRequest request;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (msg instanceof HttpRequest) {
			this.request = (HttpRequest) msg;
		}
    	if (msg instanceof LastHttpContent) {
			LastHttpContent requestBody = (LastHttpContent) msg;
			DefaultFullHttpRequest fullRequest = new DefaultFullHttpRequest(request.protocolVersion(), request.method(), request.uri(), requestBody.content(), request.headers(), requestBody.trailingHeaders());
//			requestBody.retain();
			ctx.fireChannelRead(fullRequest);
		}
    }
}
