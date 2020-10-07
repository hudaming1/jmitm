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
public class HttpsPipe extends AbstractFullPipe {

	public HttpsPipe(FrontPipe front, FullPipeHandler fullPipeHandler, WtPipeContext wtContext,
			MockHandler mockHandler) {
		super(front, fullPipeHandler, wtContext, mockHandler);
	}

	protected void connect(FullHttpRequest request) throws InterruptedException {
		InetAddress InetAddress = HttpMessageUtil.parse2InetAddress(request, true);
		wtContext.appendRequest(request);

		currentBack = super.select(InetAddress.getHost(), InetAddress.getPort());
		if (currentBack == null) {
			currentBack = initBackpipe(InetAddress, true);
		}

		if (!currentBack.isActive()) {
			currentBack.connect().addListener(f -> {
				if (!f.isSuccess()) {
					log.error("[" + wtContext.getId() + "] server connect error.", f.cause());
					wtContext.addEvent(PipeEventType.Error, "[X]与服务端" + InetAddress + "建立连接失败");
					wtContext.recordStatus(PipeStatus.Error);
					fullPipeHandler.serverConnectFailed(wtContext);
					close();
					return;
				}
				wtContext.registServer(currentBack.getChannel());
				wtContext.recordStatus(PipeStatus.Connected);
				fullPipeHandler.serverConnect(wtContext);
				wtContext.addEvent(PipeEventType.ServerConnected, "与服务端" + InetAddress + "建立连接完成");
			}).sync();
			currentBack.handshakeFuture().addListener(tls -> {
				if (!tls.isSuccess()) {
					log.error("[" + wtContext.getId() + "] server tls error", tls.cause());
					wtContext.addEvent(PipeEventType.Error, "[X]与服务端" + InetAddress + "建立连接失败");
					wtContext.recordStatus(PipeStatus.Error);
					fullPipeHandler.serverHandshakeFail(wtContext);
					close();
					return;
				}
				wtContext.addEvent(PipeEventType.ServerTlsFinish, "与服务端" + InetAddress + "握手完成");
				currentBack.getChannel().pipeline().addLast(this);
				fullPipeHandler.serverHandshakeSucc(wtContext);
			}).sync();
		}
	}
}
