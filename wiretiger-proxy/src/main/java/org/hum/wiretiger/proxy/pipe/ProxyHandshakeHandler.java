package org.hum.wiretiger.proxy.pipe;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map.Entry;

import org.hum.wiretiger.common.constant.HttpConstant;
import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.constant.Constant;
import org.hum.wiretiger.proxy.pipe.enumtype.Protocol;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;
import org.hum.wiretiger.proxy.util.HttpMessageUtil;
import org.hum.wiretiger.proxy.util.HttpMessageUtil.InetAddress;
import org.hum.wiretiger.ssl.HttpSslContextFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 *   <b>1.Channel添加FullPipe时机与应用协议相关</b>
 *     HTTP协议在解析完Request报文后才添加FullPipe，因此可能遗漏监听包含：(1)建立连接后没有触发Read；(2)连接完成后因异常关闭了连接
 *     HTTPS协议在客户端握手完成后才添加FullPipe，因此可能遗漏的监听包括：(1)同HTTP问题；(2)抛出上述1以外，握手失败也会遗漏监听
 * </pre>
 */
@Slf4j
@Sharable
public class ProxyHandshakeHandler extends SimpleChannelInboundHandler<HttpRequest> {

	private EventHandler eventHandler;
	private final int _1K = 1024;
	private final int RequestLineMaxLen = 32 * _1K;
	private final int RequestHeaderMaxLen = 8 * _1K;
	private final int RequestChunkedMaxLen = 1024 * _1K;
	
	public ProxyHandshakeHandler(EventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // [HTTP] 1.建立front连接
        WtPipeContext wtContext = WtPipeManager.get().create(ctx.channel());
        log.info("[" + wtContext.getId() + "] 1 " + ctx.channel());
        ctx.channel().attr(AttributeKey.valueOf(Constant.ATTR_PIPE)).set(wtContext);
        InetSocketAddress inetAddr = (InetSocketAddress) ctx.channel().remoteAddress();
        wtContext.setSource(inetAddr.getHostString(), inetAddr.getPort());
        eventHandler.fireConnectEvent(wtContext);
        ctx.fireChannelActive();
        ctx.pipeline().addLast(new PreFullPipe(wtContext, eventHandler));
    }

	@Override
	protected void channelRead0(ChannelHandlerContext client2ProxyCtx, HttpRequest request) throws Exception {
		
		// read host and port from http-request
		InetAddress InetAddress = HttpMessageUtil.parse2InetAddress(request, true);
		
		// wrap pipeholder
		WtPipeContext wtContext = (WtPipeContext) client2ProxyCtx.channel().attr(AttributeKey.valueOf(Constant.ATTR_PIPE)).get();
		wtContext.setTarget(InetAddress.getHost(), InetAddress.getPort());
		
    	if (HttpConstant.HTTPS_HANDSHAKE_METHOD.equalsIgnoreCase(request.method().name())) {
    		log.info("[" + wtContext.getId() + "] HTTPS 2 CONNECT " + InetAddress.getHost() + ":" + InetAddress.getPort());
    		wtContext.setProtocol(Protocol.HTTPS);
    		// SSL
    		SslHandler sslHandler = new SslHandler(HttpSslContextFactory.createSSLEngine(InetAddress.getHost()));
			sslHandler.handshakeFuture().addListener(future -> {
				if (!future.isSuccess()) {
					// java.nio.channels.ClosedChannelException时,message is null?
					client2ProxyCtx.close();
					log.error("[" + wtContext.getId() + "] handshake failure", future.cause());
					return ;
				}
				// 握手成功
	    		client2ProxyCtx.pipeline().addLast(new HttpResponseEncoder(), new HttpRequestDecoder(RequestLineMaxLen, RequestHeaderMaxLen, RequestChunkedMaxLen), new HttpObjectAggregator(Integer.MAX_VALUE));
	    		client2ProxyCtx.pipeline().addLast(new FullPipe(new FrontPipe(client2ProxyCtx.channel()), eventHandler, wtContext, true));
	    		client2ProxyCtx.pipeline().remove(PreFullPipe.class);
	    		log.info("[" + wtContext.getId() + "] client handshake success");
			});
			client2ProxyCtx.pipeline().addLast(sslHandler);
			
			// 在TLS握手前，先不要掺杂HTTP编解码器，等TLS握手完成后，统一添加HTTP编解码部分
			client2ProxyCtx.pipeline().remove(HttpRequestDecoder.class);
			client2ProxyCtx.pipeline().remove(HttpResponseEncoder.class);
			client2ProxyCtx.pipeline().remove(this);
			client2ProxyCtx.writeAndFlush(Unpooled.wrappedBuffer(HttpConstant.ConnectedLine.getBytes()));
			log.info("[" + wtContext.getId() + "] flush connectline");
    	} else {
    		wtContext.setProtocol(Protocol.HTTP);
    		
    		if (!fullPipeIsExists(client2ProxyCtx.channel())) {
    			client2ProxyCtx.pipeline().addLast(new FullPipe(new FrontPipe(client2ProxyCtx.channel()), eventHandler, wtContext, false));
    			client2ProxyCtx.pipeline().remove(this);
    		}
    		
    		deletePreFullPipeIfNesscessary(client2ProxyCtx.channel());
    		
    		// [HTTP] 2.建立back端连接
    		log.info("[" + wtContext.getId() + "] HTTP 2 CONNECT " + InetAddress.getHost() + ":" + InetAddress.getPort());
    		
    		client2ProxyCtx.fireChannelRead(request);
    	}
	}
	
	private boolean fullPipeIsExists(Channel channel) {
		Iterator<Entry<String, ChannelHandler>> iterator = channel.pipeline().iterator();
		while (iterator.hasNext()) {
			// FullPipe怎么才能作为变量传进来
			if (iterator.next().getValue() instanceof FullPipe) {
				return true;
			}
		}
		return false;
	}
	
	private void deletePreFullPipeIfNesscessary(Channel channel) {
		Iterator<Entry<String, ChannelHandler>> iterator = channel.pipeline().iterator();
		boolean needDel = false;
		while (iterator.hasNext()) {
			// FullPipe怎么才能作为变量传进来
			if (iterator.next().getValue() instanceof PreFullPipe) {
				needDel = true;
			}
		}
		if (needDel) {
			channel.pipeline().remove(PreFullPipe.class);
		}
	}
}
