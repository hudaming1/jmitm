package org.hum.jmitm.proxy.pipe.core;

import org.hum.jmitm.common.util.HttpMessageUtil;
import org.hum.jmitm.common.util.HttpMessageUtil.InetAddress;
import org.hum.jmitm.proxy.facade.PipeContext;
import org.hum.jmitm.proxy.facade.PipeInvokeChain;
import org.hum.jmitm.proxy.mock.MockHandler;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsPipeHandler extends StandardPipeHandler {

	public HttpsPipeHandler(FrontPipe front, PipeInvokeChain fullPipeHandler, PipeContext wtContext,
			MockHandler mockHandler) {
		super(front, fullPipeHandler, wtContext, mockHandler);
	}

	@Override
	protected void connect(EventLoopGroup eventLoopGroup, FullHttpRequest request) throws InterruptedException {
		InetAddress InetAddress = HttpMessageUtil.parse2InetAddress(request, true);
		if (InetAddress == null) {
			close();
			return ;
		}
		wtContext.appendRequest(request);

		currentBack = super.select(InetAddress.getHost(), InetAddress.getPort());
		if (currentBack == null) {
			currentBack = initBackpipe(eventLoopGroup, InetAddress);
		}

		if (!currentBack.isActive()) {
			currentBack.connect().addListener(f -> {
				if (!f.isSuccess()) {
					log.error("[" + wtContext.getId() + "] server connect error. cause={}", f.cause().getMessage());
					fullPipeHandler.serverConnectFailed(wtContext, f.cause());
					close();
					return;
				}
				wtContext.registServer(currentBack.getChannel());
				fullPipeHandler.serverConnect(wtContext, InetAddress);
			}).sync();
			currentBack.handshakeFuture().addListener(tls -> {
				if (!tls.isSuccess()) {
					log.error("[" + wtContext.getId() + "] server tls error, cause={}", tls.cause().getMessage(), tls.cause());
					fullPipeHandler.serverHandshakeFail(wtContext, tls.cause());
					close();
					return;
				}
				currentBack.getChannel().pipeline().addLast(this);
				fullPipeHandler.serverHandshakeSucc(wtContext);
			}).sync();
		}
	}

	@Override
	protected BackPipe initBackpipe0(EventLoopGroup eventLoopGroup, InetAddress InetAddress) {
		return new BackPipe(eventLoopGroup, InetAddress.getHost(), InetAddress.getPort(), true);
	}
}
