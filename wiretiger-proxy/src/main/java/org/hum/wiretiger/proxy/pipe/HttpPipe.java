package org.hum.wiretiger.proxy.pipe;

import org.hum.wiretiger.proxy.mock.MockHandler;
import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.chain.FullPipeHandler;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeEventType;
import org.hum.wiretiger.proxy.pipe.enumtype.PipeStatus;
import org.hum.wiretiger.proxy.util.HttpMessageUtil;
import org.hum.wiretiger.proxy.util.HttpMessageUtil.InetAddress;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpPipe extends AbstractFullPipe {

	public HttpPipe(FrontPipe front, FullPipeHandler fullPipeHandler, WtPipeContext wtContext,
			MockHandler mockHandler) {
		super(front, fullPipeHandler, wtContext, mockHandler);
	}

	protected void connect(FullHttpRequest request) throws InterruptedException {
		InetAddress InetAddress = HttpMessageUtil.parse2InetAddress(request, false);
		wtContext.appendRequest(request);

		currentBack = super.select(InetAddress.getHost(), InetAddress.getPort());
		if (currentBack == null) {
			currentBack = initBackpipe(InetAddress);
		}
		if (!currentBack.isActive()) {
			currentBack.connect().addListener(f -> {
				if (!f.isSuccess()) {
					log.error("[" + wtContext.getId() + "] server connect error. cause={}", f.cause().getMessage());
					wtContext.addEvent(PipeEventType.Error, "[X]与服务端" + InetAddress + "建立连接失败");
					wtContext.recordStatus(PipeStatus.Error);
					fullPipeHandler.serverConnect(wtContext);
					close();
					return;
				}
				wtContext.registServer(currentBack.getChannel());
				wtContext.recordStatus(PipeStatus.Connected);
				fullPipeHandler.serverConnectFailed(wtContext);
				wtContext.addEvent(PipeEventType.ServerConnected, "与服务端" + InetAddress + "建立连接完成");
				currentBack.getChannel().pipeline().addLast(this);
			}).sync();
		}
	}

	@Override
	protected BackPipe initBackpipe0(InetAddress InetAddress) {
		return new BackPipe(InetAddress.getHost(), InetAddress.getPort(), false);
	}
}
