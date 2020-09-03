package org.hum.wiretiger.proxy.pipe;

import org.hum.wiretiger.common.constant.HttpConstant;
import org.hum.wiretiger.proxy.pipe.bean.WtPipeHolder;
import org.hum.wiretiger.proxy.pipe.constant.Constant;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.Protocol;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;
import org.hum.wiretiger.proxy.util.NettyUtils;
import org.hum.wiretiger.ssl.HttpSslContextFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class HttpProxyHandshakeHandler extends SimpleChannelInboundHandler<HttpRequest> {

	private static final String ConnectedLine = "HTTP/1.1 200 Connection established\r\n\r\n";
	private static final String HTTPS_HANDSHAKE_METHOD = "CONNECT";
	
	private EventHandler eventHandler;
	
	public HttpProxyHandshakeHandler(EventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        // init pipe
        WtPipeHolder pipeHolder = WtPipeManager.get().create(ctx.channel());
        ctx.channel().attr(AttributeKey.valueOf(Constant.ATTR_PIPE)).set(pipeHolder);
        eventHandler.fireConnectEvent(pipeHolder);
    }

	@Override
	protected void channelRead0(ChannelHandlerContext client2ProxyCtx, HttpRequest request) throws Exception {
		
		// read host and port from http-request
		String[] hostAndPort = request.headers().get(HttpConstant.Host).split(":");
		String host = hostAndPort[0];
		int port = guessPort(request.method().name(), hostAndPort);
		
		// wrap pipeholder
		WtPipeHolder pipeHolder = (WtPipeHolder) client2ProxyCtx.channel().attr(AttributeKey.valueOf(Constant.ATTR_PIPE)).get();
		
    	if (HTTPS_HANDSHAKE_METHOD.equalsIgnoreCase(request.method().name())) {
    		log.info("HTTPS " + host + ":" + port);
    		pipeHolder.setProtocol(Protocol.HTTPS);
    		pipeHolder.setName(Protocol.HTTPS + "://" + host + ":" + port);
    		pipeHolder.addEvent(PipeEventType.Parsed, "解析目标服务器请求为" + host + ":" + port);
    		// 根据域名颁发证书
			SslHandler sslHandler = new SslHandler(HttpSslContextFactory.createSSLEngine(host));
			sslHandler.handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
				@Override
				public void operationComplete(Future<? super Channel> future) throws Exception {
					if (!future.isSuccess()) {
						log.error("{}, handshake failed", hostAndPort, future.cause());
						return ;
					}
		    		client2ProxyCtx.pipeline().addLast(new HttpServerCodec());
					client2ProxyCtx.pipeline().addLast(new DefaultPipeHandler(eventHandler, pipeHolder, host, port));
				}
			});
			client2ProxyCtx.pipeline().addLast(sslHandler);
			// 在TLS握手前，先不要掺杂HTTP编解码器，等TLS握手完成后，统一添加HTTP编解码部分
			client2ProxyCtx.pipeline().remove(HttpRequestDecoder.class);
			// 握手后，对于HTTPS而言，这个handler就失去了意义
			client2ProxyCtx.pipeline().remove(this);
			client2ProxyCtx.pipeline().firstContext().writeAndFlush(Unpooled.wrappedBuffer(ConnectedLine.getBytes()));
    	} else {
    		// new HttpForwad(client2ProxyCtx.channel(), eventHandler, host, port).start();
    		log.info(host + ":" + port + " - " + NettyUtils.toHostAndPort(client2ProxyCtx.channel()));
    		pipeHolder.addEvent(PipeEventType.Parsed, "解析目标服务器请求为" + request.uri());
    		pipeHolder.setProtocol(Protocol.HTTP);
    		pipeHolder.setName(request.uri());
    		client2ProxyCtx.pipeline().addLast(new HttpResponseEncoder());
    		client2ProxyCtx.pipeline().addLast(new DefaultPipeHandler(eventHandler, pipeHolder, host, port));
    		log.info(NettyUtils.toHostAndPort(client2ProxyCtx.channel()) + " add pipeline ");
    		client2ProxyCtx.fireChannelRead(request);
    	}
	}
	
	private int guessPort(String method, String[] hostAndPort) {
		if (hostAndPort.length == 2) {
			return Integer.parseInt(hostAndPort[1]);
		} else if (HTTPS_HANDSHAKE_METHOD.equalsIgnoreCase(method)) {
			return HttpConstant.DEFAULT_HTTPS_PORT;
		} else {
			return HttpConstant.DEFAULT_HTTP_PORT;
		}
	}
}
