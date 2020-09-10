package org.hum.wiretiger.proxy.pipe;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsFullPipe extends FullPipe {

	public HttpsFullPipe(FrontPipe front, BackPipe back, EventHandler eventHandler, WtPipeContext wtContext) {
		super(front, eventHandler, wtContext);
	}

	public void fireClientTlsHandshakeFailure(Throwable cause) {
		wtContext.addEvent(PipeEventType.ClientClosed, "客户端TLS握手失败：" + cause.getMessage());
		wtContext.recordStatus(PipeStatus.Closed);
		eventHandler.fireDisconnectEvent(wtContext);
		close();
//		log.error("[" + wtContext.getId() + "]{}, client-tls handshake failed, close pipe", back.getHost() + ":" + back.getPort(), cause);		
	}

	public void fireClientTlsHandshakeSuccess() {
		wtContext.addEvent(PipeEventType.ClientTlsFinish, "客户端TLS握手完成");
		eventHandler.fireChangeEvent(wtContext);
	}

	public void fireServerActiveFailure(Throwable cause) {
		close();
		wtContext.addEvent(PipeEventType.ServerClosed, "服务端建立连接失败：" + cause.getMessage());
		wtContext.recordStatus(PipeStatus.Closed);
		eventHandler.fireDisconnectEvent(wtContext);
//		log.error("[" + wtContext.getId() + "]{}, server-tls handshake failed, close pipe", back.getHost() + ":" + back.getPort(), cause);
	}

//	@Override
//	public void channelRead4Client(ChannelHandlerContext ctx, Object msg) throws Exception {
//		if (msg instanceof HttpRequest) {
//			wtContext.addEvent(PipeEventType.Read, "读取客户端请求，DefaultHttpRequest");
//			wtContext.appendRequest((HttpRequest) msg);
//
//			// connect server
//			connect().addListener(future -> {
//				// 连接失败
//				if (!future.isSuccess()) {
//					fireServerActiveFailure(future.cause());
//					return; 
//				}  
//			});
//		} else if (msg instanceof LastHttpContent) {
//			wtContext.addEvent(PipeEventType.Read, "读取客户端请求，LastHttpContent");
//		} else {
//			log.warn("need support more types, find type=" + msg.getClass());
//		}
//		super.back.getChannel().writeAndFlush(msg);
//		wtContext.recordStatus(PipeStatus.Read);
//		eventHandler.fireChangeEvent(wtContext);
//	}

	@Override
	public ChannelFuture connect() {
//		return super.connect().addListener(f->{
//			back.handshakeFuture().addListener(new GenericFutureListener<Future<Channel>>() {
//				@Override
//				public void operationComplete(Future<Channel> future) throws Exception {
//					if (!future.isSuccess()) {
//						log.error("[" + wtContext.getId() + "] server tls error,", future);
//						wtContext.recordStatus(PipeStatus.Closed);
//						wtContext.addEvent(PipeEventType.ServerClosed, "服务端TLS握手失败：" + future.cause().getMessage());
//						eventHandler.fireChangeEvent(wtContext);
//						return ;
//					}
//					wtContext.addEvent(PipeEventType.ServerTlsFinish, "服务端TLS握手完成");
//					eventHandler.fireChangeEvent(wtContext);
//				}
//			});
//		});
		return null;
	}
}
