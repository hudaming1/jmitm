package org.hum.wiretiger.proxy.pipe.chain;

import org.hum.wiretiger.proxy.facade.PipeInvokeChain;
import org.hum.wiretiger.proxy.facade.WtPipeContext;
import org.hum.wiretiger.proxy.util.HttpMessageUtil.InetAddress;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class DefaultPipeInvokeChain extends PipeInvokeChain {

	public DefaultPipeInvokeChain(PipeInvokeChain next) {
		super(next);
	}

	@Override
	protected void clientConnect0(WtPipeContext ctx) {
	}

	@Override
	protected void clientParsed0(WtPipeContext ctx) {
	}

	@Override
	protected void clientRead0(WtPipeContext ctx, FullHttpRequest request) {
	}

	@Override
	protected void serverConnect0(WtPipeContext ctx, InetAddress InetAddress) {
	}

	@Override
	protected void serverHandshakeSucc0(WtPipeContext ctx) {
	}

	@Override
	protected void serverFlush0(WtPipeContext ctx, FullHttpRequest request) {
	}

	@Override
	protected void serverRead0(WtPipeContext ctx, FullHttpResponse response) {
	}

	@Override
	protected void clientFlush0(WtPipeContext ctx, FullHttpResponse response) {
	}

	@Override
	protected void clientClose0(WtPipeContext ctx) {
	}

	@Override
	protected void serverClose0(WtPipeContext ctx) {
	}

	@Override
	protected void clientError0(WtPipeContext ctx, Throwable cause) {
	}

	@Override
	protected void serverError0(WtPipeContext ctx, Throwable cause) {
	}

	@Override
	protected void clientHandshakeSucc0(WtPipeContext ctx) {
	}

	@Override
	protected void clientHandshakeFail0(WtPipeContext ctx, Throwable cause) {
	}

	@Override
	protected void serverConnectFailed0(WtPipeContext ctx, Throwable cause) {
	}

	@Override
	protected void serverHandshakeFail0(WtPipeContext ctx, Throwable cause) {
	}

}
