package org.hum.wiretiger.proxy.pipe;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;
import org.hum.wiretiger.proxy.pipe.event.EventHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsFullPipe extends FullPipe {

	public HttpsFullPipe(FrontPipe front, BackPipe back, EventHandler eventHandler, WtPipeContext wtContext) {
		super(front, back, eventHandler, wtContext);
	}

	public void fireClientTlsHandshakeFailure(Throwable cause) {
		wtContext.addEvent(PipeEventType.ClientClosed, "客户端TLS握手失败：" + cause.getMessage());
		wtContext.recordStatus(PipeStatus.Closed);
		eventHandler.fireDisconnectEvent(wtContext);
		close();
		log.error("[" + wtContext.getId() + "]{}, client-tls handshake failed, close pipe", back.getHost() + ":" + back.getPort(), cause);		
	}

	public void fireClientGlsHandshakeSuccess() {
		wtContext.addEvent(PipeEventType.ClientTlsFinish, "客户端TLS握手完成");
		eventHandler.fireChangeEvent(wtContext);
	}

	public void fireServerActiveFailure(Throwable cause) {
		close();
		wtContext.addEvent(PipeEventType.ServerClosed, "服务端建立连接失败：" + cause.getMessage());
		wtContext.recordStatus(PipeStatus.Closed);
		eventHandler.fireDisconnectEvent(wtContext);
		log.error("[" + wtContext.getId() + "]{}, server-tls handshake failed, close pipe", back.getHost() + ":" + back.getPort(), cause);
	}

	public ChannelFuture connect() {
		return super.connect().addListener(f->{
			back.handshakeFuture().addListener(new GenericFutureListener<Future<Channel>>() {
				@Override
				public void operationComplete(Future<Channel> future) throws Exception {
					if (!future.isSuccess()) {
						log.error("[" + wtContext.getId() + "] server tls error,", future);
						wtContext.recordStatus(PipeStatus.Closed);
						wtContext.addEvent(PipeEventType.ServerClosed, "服务端TLS握手失败：" + future.cause().getMessage());
						eventHandler.fireChangeEvent(wtContext);
						return ;
					}
					wtContext.addEvent(PipeEventType.ServerTlsFinish, "服务端TLS握手完成");
					eventHandler.fireChangeEvent(wtContext);
				}
			});
		});
	}
}
