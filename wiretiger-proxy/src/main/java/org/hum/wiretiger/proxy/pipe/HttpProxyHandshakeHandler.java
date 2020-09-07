package org.hum.wiretiger.proxy.pipe;

import java.util.Iterator;
import java.util.Map.Entry;

import org.hum.wiretiger.common.constant.HttpConstant;
import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.constant.Constant;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;
import org.hum.wiretiger.proxy.pipe.enumtype.Protocol;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;
import org.hum.wiretiger.ssl.HttpSslContextFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class HttpProxyHandshakeHandler extends SimpleChannelInboundHandler<HttpRequest> {

	private EventHandler eventHandler;
	
	public HttpProxyHandshakeHandler(EventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // [HTTP] 1.建立front连接
        WtPipeContext wtContext = WtPipeManager.get().create(ctx.channel());
        log.info("[" + wtContext.getId() + "] 1");
        ctx.channel().attr(AttributeKey.valueOf(Constant.ATTR_PIPE)).set(wtContext);
        eventHandler.fireConnectEvent(wtContext);
        ctx.fireChannelActive();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext client2ProxyCtx, HttpRequest request) throws Exception {
		
		// read host and port from http-request
		String[] hostAndPort = request.headers().get(HttpConstant.Host).split(":");
		String host = hostAndPort[0];
		int port = guessPort(request.method().name(), hostAndPort);
		
		// wrap pipeholder
		WtPipeContext wtContext = (WtPipeContext) client2ProxyCtx.channel().attr(AttributeKey.valueOf(Constant.ATTR_PIPE)).get();
		
    	if (HttpConstant.HTTPS_HANDSHAKE_METHOD.equalsIgnoreCase(request.method().name())) {
    		log.info("[" + wtContext.getId() + "] HTTPS 2 CONNECT " + host + ":" + port);
    		wtContext.setProtocol(Protocol.HTTPS);
    		// 根据域名颁发证书
			BackPipe back = new BackPipe(host, port, true);
    		FullPipe full = new FullPipe(new FrontPipe(client2ProxyCtx.channel()), back, eventHandler, wtContext);
    		// SSL
    		SslHandler sslHandler = new SslHandler(HttpSslContextFactory.createSSLEngine(host));
			sslHandler.handshakeFuture().addListener(future -> {
				if (!future.isSuccess()) {
					wtContext.addEvent(PipeEventType.ClientClosed, "客户端TLS握手失败：" + future.cause().getLocalizedMessage());
					wtContext.recordStatus(PipeStatus.Closed);
					eventHandler.fireDisconnectEvent(wtContext);
					full.close();
					log.error("[" + wtContext.getId() + "]{}, client-tls handshake failed, close pipe", hostAndPort, future.cause());
					return ;
				}
				wtContext.addEvent(PipeEventType.ClientTlsFinish, "客户端TLS握手完成");
				eventHandler.fireChangeEvent(wtContext);
	    		client2ProxyCtx.pipeline().addLast(new HttpServerCodec());
	    		client2ProxyCtx.pipeline().addLast(full);
			});
			client2ProxyCtx.pipeline().addLast(sslHandler);
			
			// 在TLS握手前，先不要掺杂HTTP编解码器，等TLS握手完成后，统一添加HTTP编解码部分
			client2ProxyCtx.pipeline().remove(HttpRequestDecoder.class);
			client2ProxyCtx.pipeline().remove(HttpResponseEncoder.class);
			client2ProxyCtx.pipeline().remove(this);
			
			// 打通全链路后，给客户端发送200完成请求，告知可以发送业务数据
			full.connect().addListener(future -> {
				// 连接失败
				if (!future.isSuccess()) {
					full.close();
					wtContext.addEvent(PipeEventType.ServerClosed, "服务端建立连接失败：" + future.cause().getLocalizedMessage());
					wtContext.recordStatus(PipeStatus.Closed);
					eventHandler.fireDisconnectEvent(wtContext);
					log.error("[" + wtContext.getId() + "]{}, server-tls handshake failed, close pipe", hostAndPort, future.cause());
					return; 
				}  
				wtContext.addEvent(PipeEventType.ServerTlsFinish, "服务端建立连接完成");
				wtContext.recordStatus(PipeStatus.Connected);
				eventHandler.fireChangeEvent(wtContext);
				client2ProxyCtx.writeAndFlush(Unpooled.wrappedBuffer(HttpConstant.ConnectedLine.getBytes()));
			});
    	} else {
    		wtContext.setProtocol(Protocol.HTTP);
    		BackPipe back = new BackPipe(host, port, false);
    		FullPipe full = new FullPipe(new FrontPipe(client2ProxyCtx.channel()), back, eventHandler, wtContext);
    		deleteFullPipeIfNesscessary(client2ProxyCtx.channel());
    		client2ProxyCtx.pipeline().addLast(full);
    		// [HTTP] 2.建立back端连接
    		log.info("[" + wtContext.getId() + "] HTTP 2 CONNECT " + host + ":" + port);
    		full.connect().addListener(future-> {
    			if (!future.isSuccess()) {
    				return ;
    			}
    			// [HTTP] 4.将front收到的Request转发给back
    			back.getChannel().writeAndFlush(request);
    			log.info("[" + wtContext.getId() + "] 4");
    		});
    	}
	}
	
	private void deleteFullPipeIfNesscessary(Channel channel) {
		Iterator<Entry<String, ChannelHandler>> iterator = channel.pipeline().iterator();
		boolean needDel = false;
		while (iterator.hasNext()) {
			// FullPipe怎么才能作为变量传进来
			if (iterator.next().getValue() instanceof FullPipe) {
				needDel = true;
			}
		}
		if (needDel) {
			channel.pipeline().remove(FullPipe.class);
		}
	}
	
	private int guessPort(String method, String[] hostAndPort) {
		if (hostAndPort.length == 2) {
			return Integer.parseInt(hostAndPort[1]);
		} else if (HttpConstant.HTTPS_HANDSHAKE_METHOD.equalsIgnoreCase(method)) {
			return HttpConstant.DEFAULT_HTTPS_PORT;
		} else {
			return HttpConstant.DEFAULT_HTTP_PORT;
		}
	}
}
