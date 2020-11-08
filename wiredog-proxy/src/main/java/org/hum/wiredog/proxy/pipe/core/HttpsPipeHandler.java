package org.hum.wiredog.proxy.pipe.core;

import org.hum.wiredog.common.util.HttpMessageUtil;
import org.hum.wiredog.common.util.HttpMessageUtil.InetAddress;
import org.hum.wiredog.proxy.facade.PipeInvokeChain;
import org.hum.wiredog.proxy.facade.WtPipeContext;
import org.hum.wiredog.proxy.mock.MockHandler;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsPipeHandler extends StandardPipeHandler {

	public HttpsPipeHandler(FrontPipe front, PipeInvokeChain fullPipeHandler, WtPipeContext wtContext,
			MockHandler mockHandler) {
		super(front, fullPipeHandler, wtContext, mockHandler);
	}

	protected void connect(FullHttpRequest request) throws InterruptedException {
		InetAddress InetAddress = HttpMessageUtil.parse2InetAddress(request, true);
		if (InetAddress == null) {
			close();
			return ;
		}
		wtContext.appendRequest(request);

		currentBack = super.select(InetAddress.getHost(), InetAddress.getPort());
		if (currentBack == null) {
			currentBack = initBackpipe(InetAddress);
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
					log.error("[" + wtContext.getId() + "] server tls error, cause={}", tls.cause().getMessage());
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
	protected BackPipe initBackpipe0(InetAddress InetAddress) {
		return new BackPipe(InetAddress.getHost(), InetAddress.getPort(), true);
	}
}
