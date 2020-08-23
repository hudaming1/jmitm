package org.hum.wiretiger.core.pipe;

import org.hum.wiretiger.common.Constant;
import org.hum.wiretiger.common.enumtype.Protocol;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.pipe.event.EventHandler;
import org.hum.wiretiger.core.ssl.HttpSslContextFactory;
import org.hum.wiretiger.http.bean.HttpRequest;
import org.hum.wiretiger.http.common.HttpHelper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpProxyHandshakeHandler extends ChannelInboundHandlerAdapter {

	private static final String ConnectedLine = "HTTP/1.1 200 Connection established\r\n\r\n";
	private static final String HTTPS_HANDSHAKE_METHOD = "CONNECT";
	private volatile boolean isParsed = false;
	private EventHandler eventHandler;
	
	public HttpProxyHandshakeHandler(EventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	// TODO pipe connect
        ctx.fireChannelActive();
        PipeHolder pipeHolder = PipeManager.get().create(ctx.channel());
        ctx.channel().attr(AttributeKey.valueOf(Constant.ATTR_PIPE)).set(pipeHolder);
        eventHandler.fireConnectEvent(pipeHolder);
    }
	
	@Override
	public void channelRead(ChannelHandlerContext client2ProxyCtx, Object msg) throws Exception {
    	if (isParsed) {
    		client2ProxyCtx.fireChannelRead(msg);
    		return ;
    	}
    	// 是否再需要增加parsing状态过渡？
    	isParsed = true;
    	
    	// 握完手这个handler就没有用了，直接删除
		client2ProxyCtx.pipeline().remove(this);
		
		PipeHolder pipeHolder = (PipeHolder) client2ProxyCtx.channel().attr(AttributeKey.valueOf(Constant.ATTR_PIPE)).get();
		HttpRequest request = HttpHelper.decode((ByteBuf) msg);
		pipeHolder.setName(client2ProxyCtx.channel().remoteAddress().toString() + "->" + request.getHost() + ":" + request.getPort());
    	
    	if (HTTPS_HANDSHAKE_METHOD.equalsIgnoreCase(request.getMethod())) {
    		pipeHolder.setProtocol(Protocol.HTTPS);
    		// 根据域名颁发证书
			SslHandler sslHandler = new SslHandler(HttpSslContextFactory.createSSLEngine(request.getHost()));
			sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
				@Override
				public void operationComplete(Future<? super Channel> future) throws Exception {
		    		client2ProxyCtx.pipeline().addLast(new HttpServerCodec());
					client2ProxyCtx.pipeline().addLast(new DefaultPipeHandler(eventHandler, pipeHolder, request.getHost(), request.getPort()));
				}
			});
			client2ProxyCtx.pipeline().addLast(sslHandler);
			client2ProxyCtx.pipeline().firstContext().writeAndFlush(Unpooled.wrappedBuffer(ConnectedLine.getBytes()));
    	} else {
    		// HTTP 拦截器Mock逻辑加在这里
    		// TODO 
    		pipeHolder.setProtocol(Protocol.HTTP);
    		client2ProxyCtx.pipeline().addLast(new HttpServerCodec());
    		client2ProxyCtx.pipeline().addLast(new DefaultPipeHandler(eventHandler, pipeHolder, request.getHost(), request.getPort()));
    		client2ProxyCtx.pipeline().fireChannelRead(msg);
    	}
	}
}
