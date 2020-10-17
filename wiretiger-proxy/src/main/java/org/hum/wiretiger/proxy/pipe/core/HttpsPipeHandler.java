package org.hum.wiretiger.proxy.pipe.core;

import java.util.concurrent.CountDownLatch;

import org.hum.wiretiger.proxy.facade.PipeInvokeChain;
import org.hum.wiretiger.proxy.facade.WtPipeContext;
import org.hum.wiretiger.proxy.mock.MockHandler;
import org.hum.wiretiger.proxy.util.HttpMessageUtil;
import org.hum.wiretiger.proxy.util.HttpMessageUtil.InetAddress;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsPipeHandler extends StandardPipeHandler {

	public HttpsPipeHandler(FrontPipe front, PipeInvokeChain fullPipeHandler, WtPipeContext wtContext,
			MockHandler mockHandler) {
		super(front, fullPipeHandler, wtContext, mockHandler);
	}

	@Override
	protected void connect(EventLoop eventLoop, FullHttpRequest request) throws InterruptedException {
		InetAddress InetAddress = HttpMessageUtil.parse2InetAddress(request, true);
		if (InetAddress == null) {
			close();
			return ;
		}
		wtContext.appendRequest(request);

		currentBack = super.select(InetAddress.getHost(), InetAddress.getPort());
		if (currentBack == null) {
			currentBack = initBackpipe(eventLoop, InetAddress);
		}

		if (!currentBack.isActive()) {
			CountDownLatch latch1 = new CountDownLatch(1);
			CountDownLatch latch2 = new CountDownLatch(1);
			currentBack.connect().addListener(f -> {
				System.out.println("2");
				if (!f.isSuccess()) {
					log.error("[" + wtContext.getId() + "] server connect error. cause={}", f.cause().getMessage());
					fullPipeHandler.serverConnectFailed(wtContext, f.cause());
					close();
					latch1.countDown();
					return;
				}
				wtContext.registServer(currentBack.getChannel());
				fullPipeHandler.serverConnect(wtContext, InetAddress);
				latch1.countDown();
			});
			currentBack.handshakeFuture().addListener(tls -> {
				System.out.println("4");
				if (!tls.isSuccess()) {
					log.error("[" + wtContext.getId() + "] server tls error, cause={}", tls.cause().getMessage());
					fullPipeHandler.serverHandshakeFail(wtContext, tls.cause());
					close();
					latch2.countDown();
					return;
				}
				currentBack.getChannel().pipeline().addLast(this);
				fullPipeHandler.serverHandshakeSucc(wtContext);
				latch2.countDown();
			});
			System.out.println("1");
			latch1.await();
			System.out.println("3");
			latch2.await();
		}
	}

	@Override
	protected BackPipe initBackpipe0(EventLoop eventLoop, InetAddress InetAddress) {
		return new BackPipe(eventLoop, InetAddress.getHost(), InetAddress.getPort(), true);
	}
}
