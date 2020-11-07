package org.hum.wiredog.proxy.pipe.core;

import java.util.concurrent.atomic.AtomicInteger;

import org.hum.wiredog.common.constant.HttpConstant;
import org.hum.wiredog.proxy.facade.PipeInvokeChain;
import org.hum.wiredog.proxy.facade.WtPipeContext;
import org.hum.wiredog.proxy.mock.MockHandler;
import org.hum.wiredog.proxy.pipe.constant.Constant;
import org.hum.wiredog.proxy.pipe.enumtype.Protocol;
import org.hum.wiredog.proxy.server.WtDefaultServer;
import org.hum.wiredog.proxy.util.HttpMessageUtil;
import org.hum.wiredog.proxy.util.HttpMessageUtil.InetAddress;
import org.hum.wiredog.proxy.util.NettyUtils;
import org.hum.wiredog.ssl.HttpSslContextFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyHandshakeHandler extends SimpleChannelInboundHandler<HttpRequest> {

	private static final AtomicInteger counter = new AtomicInteger(1);
	private MockHandler mockHandler;
	private PipeInvokeChain fullPipeHandler;
	private final int _1K = 1024;
	private final int RequestLineMaxLen = 32 * _1K;
	private final int RequestHeaderMaxLen = 8 * _1K;
	private final int RequestChunkedMaxLen = 1024 * _1K;
	
	public ProxyHandshakeHandler(PipeInvokeChain fullPipeHandler, MockHandler mockHandler) {
		this.fullPipeHandler = fullPipeHandler;
		this.mockHandler = mockHandler;
	}
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // [HTTP] 1.建立front连接
		InetAddress inetAddr = NettyUtils.toHostAndPort(ctx.channel());
		// init context
		WtPipeContext wtContext = new WtPipeContext(counter.getAndIncrement(), ctx.channel());
		wtContext.setName(inetAddr.toString());
        wtContext.setSource(inetAddr.getHost(), inetAddr.getPort());
        ctx.channel().attr(AttributeKey.valueOf(Constant.ATTR_PIPE)).set(wtContext);
        ctx.pipeline().addLast(new InactiveChannelHandler(wtContext, fullPipeHandler));
        fullPipeHandler.clientConnect(wtContext);
    }

	@Override
	protected void channelRead0(ChannelHandlerContext client2ProxyCtx, HttpRequest request) throws Exception {
		
		// read host and port from http-request
		InetAddress InetAddress = HttpMessageUtil.parse2InetAddress(request, true);
		if (InetAddress == null) {
			client2ProxyCtx.close();
			return ;
		}
		
		// wrap pipeholder
		WtPipeContext wtContext = (WtPipeContext) client2ProxyCtx.channel().attr(AttributeKey.valueOf(Constant.ATTR_PIPE)).get();
		wtContext.setTarget(InetAddress.getHost(), InetAddress.getPort());
		wtContext.setProtocol(HttpMessageUtil.isHttpsRequest(request) ? Protocol.HTTPS : Protocol.HTTP);
		fullPipeHandler.clientParsed(wtContext);
		
    	if (wtContext.getProtocol() == Protocol.HTTPS) {
    		if (WtDefaultServer.config.isParseHttps()) {
        		// SSL
        		SslHandler sslHandler = new SslHandler(HttpSslContextFactory.createSSLEngine(InetAddress.getHost()));
    			sslHandler.handshakeFuture().addListener(sslHandshakeResult -> {
    				if (!sslHandshakeResult.isSuccess()) {
    					fullPipeHandler.clientHandshakeFail(wtContext, sslHandshakeResult.cause());
    					client2ProxyCtx.close();
    					log.error("[" + wtContext.getId() + "] handshake failure, cause=" + sslHandshakeResult.cause().getMessage());
    					return ;
    				}
    				// SSL握手成功
    	    		client2ProxyCtx.pipeline().addLast(
    	    				new HttpResponseEncoder(), 
    	    				new HttpRequestDecoder(RequestLineMaxLen, RequestHeaderMaxLen, RequestChunkedMaxLen), 
    	    				new HttpObjectAggregator(Integer.MAX_VALUE),
    	    				new FullRequestDecoder());
    	    		client2ProxyCtx.pipeline().addLast(new HttpsPipeHandler(new FrontPipe(client2ProxyCtx.channel()), fullPipeHandler, wtContext, mockHandler));
    	    		client2ProxyCtx.pipeline().remove(InactiveChannelHandler.class);
    	    		fullPipeHandler.clientHandshakeSucc(wtContext);
    			});
    			client2ProxyCtx.pipeline().addLast(sslHandler);
    		} else {
    			client2ProxyCtx.pipeline().addLast(new SimpleForwardPipeHandler(wtContext));
    		}
			
			// 在TLS握手前，先不要掺杂HTTP编解码器，等TLS握手完成后，统一添加HTTP编解码部分
			client2ProxyCtx.pipeline().remove(HttpRequestDecoder.class);
			client2ProxyCtx.pipeline().remove(HttpResponseEncoder.class);
			client2ProxyCtx.pipeline().remove(FullRequestDecoder.class);
			client2ProxyCtx.pipeline().remove(this);
			client2ProxyCtx.writeAndFlush(Unpooled.wrappedBuffer(HttpConstant.ConnectedLine.getBytes()));
    	} else {

    		if (NettyUtils.findChannelHandler(client2ProxyCtx.channel(), HttpPipeHandler.class) == null) {
    			client2ProxyCtx.pipeline().addLast(new HttpPipeHandler(new FrontPipe(client2ProxyCtx.channel()), fullPipeHandler, wtContext, mockHandler));
    			client2ProxyCtx.pipeline().remove(this);
    		}
    		
    		ChannelHandler inactiveChannelHandler = NettyUtils.findChannelHandler(client2ProxyCtx.channel(), InactiveChannelHandler.class);
    		if (inactiveChannelHandler != null) {
    			client2ProxyCtx.pipeline().remove(inactiveChannelHandler);
    		}
    		
    		// [HTTP] 2.建立back端连接
    		client2ProxyCtx.fireChannelRead(request);
    	}
	}
}
