package org.hum.wiretiger.core.handler;

import org.hum.wiretiger.common.Constant;
import org.hum.wiretiger.common.enumtype.Protocol;
import org.hum.wiretiger.core.handler.bean.HttpRequest;
import org.hum.wiretiger.core.handler.helper.HttpHelper;
import org.hum.wiretiger.core.pipe.DefaultPipeHandler;
import org.hum.wiretiger.core.pipe.PipeManager;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.ssl.HttpSslContextFactory;

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
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        ctx.channel().attr(AttributeKey.valueOf(Constant.ATTR_PIPE)).set(PipeManager.get().create(ctx.channel()));
    }
	
	@Override
	public void channelRead(ChannelHandlerContext client2ProxyCtx, Object msg) throws Exception {
    	if (isParsed) {
    		client2ProxyCtx.fireChannelRead(msg);
    		return ;
    	}
    	// 是否再需要增加parsing状态过渡？
    	isParsed = true;
		client2ProxyCtx.pipeline().remove(this);
		
		PipeHolder pipeHolder = (PipeHolder) client2ProxyCtx.channel().attr(AttributeKey.valueOf(Constant.ATTR_PIPE)).get();
    	
    	HttpRequest request = HttpHelper.decode((ByteBuf) msg);
    	if (HTTPS_HANDSHAKE_METHOD.equalsIgnoreCase(request.getMethod())) {
    		pipeHolder.setProtocol(Protocol.HTTPS);
    		
    		// 根据域名颁发证书
			SslHandler sslHandler = new SslHandler(HttpSslContextFactory.createSSLEngine(request.getHost()));
			sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
				@Override
				public void operationComplete(Future<? super Channel> future) throws Exception {
		    		client2ProxyCtx.pipeline().addLast(new HttpServerCodec());
					client2ProxyCtx.pipeline().addLast(new DefaultPipeHandler(pipeHolder, request.getHost(), request.getPort()));
					client2ProxyCtx.pipeline().firstContext().writeAndFlush(Unpooled.wrappedBuffer(ConnectedLine.getBytes()));
				}
			});
			client2ProxyCtx.pipeline().addLast(sslHandler);
    	} else {
    		pipeHolder.setProtocol(Protocol.HTTP);
    		client2ProxyCtx.pipeline().addLast(new HttpServerCodec());
    		client2ProxyCtx.pipeline().addLast(new DefaultPipeHandler(pipeHolder, request.getHost(), request.getPort()));
    		client2ProxyCtx.pipeline().fireChannelRead(msg);
    	}
	}
}
