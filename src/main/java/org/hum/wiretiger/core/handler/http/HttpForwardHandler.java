package org.hum.wiretiger.core.handler.http;

import org.hum.wiretiger.core.pipe.bean.PipeHolder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpForwardHandler extends SimpleChannelInboundHandler<HttpObject> {
	
	private PipeHolder pipeHolder;
	
	public HttpForwardHandler(PipeHolder pipeHolder, String host, int port) {
		super(false);
		this.pipeHolder = pipeHolder;
		try {
			new Forward(pipeHolder, host, port).start().sync();
		} catch (InterruptedException e) {
			log.error("", e);
		}
	}

	@Override
	public void channelRead0(ChannelHandlerContext sourceCtx, HttpObject clientRequest) throws Exception {
		this.pipeHolder.onReadRequest(clientRequest);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		this.pipeHolder.onError4FrontChannel();
	}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        this.pipeHolder.onDisconnect4FrontChannel();
    }
}
