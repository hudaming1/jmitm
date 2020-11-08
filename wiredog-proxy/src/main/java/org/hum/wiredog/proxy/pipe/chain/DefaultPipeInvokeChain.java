package org.hum.wiredog.proxy.pipe.chain;

import org.hum.wiredog.common.util.HttpMessageUtil.InetAddress;
import org.hum.wiredog.proxy.facade.PipeInvokeChain;
import org.hum.wiredog.proxy.facade.WtPipeContext;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class DefaultPipeInvokeChain extends PipeInvokeChain {

	public DefaultPipeInvokeChain(PipeInvokeChain next) {
		super(next);
	}

	@Override
	protected boolean clientConnect0(WtPipeContext ctx) {
		return true;
	}

	@Override
	protected boolean clientParsed0(WtPipeContext ctx) {
		return true;
	}

	@Override
	protected boolean clientRead0(WtPipeContext ctx, FullHttpRequest request) {
		return true;
	}

	@Override
	protected boolean serverConnect0(WtPipeContext ctx, InetAddress InetAddress) {
		return true;
	}

	@Override
	protected boolean serverHandshakeSucc0(WtPipeContext ctx) {
		return true;
	}

	@Override
	protected boolean serverFlush0(WtPipeContext ctx, FullHttpRequest request) {
		return true;
	}

	@Override
	protected boolean serverRead0(WtPipeContext ctx, FullHttpResponse response) {
		return true;
	}

	@Override
	protected boolean clientFlush0(WtPipeContext ctx, FullHttpResponse response) {
		return true;
	}

	@Override
	protected boolean clientClose0(WtPipeContext ctx) {
		return true;
	}

	@Override
	protected boolean serverClose0(WtPipeContext ctx) {
		return true;
	}

	@Override
	protected boolean clientError0(WtPipeContext ctx, Throwable cause) {
		return true;
	}

	@Override
	protected boolean serverError0(WtPipeContext ctx, Throwable cause) {
		return true;
	}

	@Override
	protected boolean clientHandshakeSucc0(WtPipeContext ctx) {
		return true;
	}

	@Override
	protected boolean clientHandshakeFail0(WtPipeContext ctx, Throwable cause) {
		return true;
	}

	@Override
	protected boolean serverConnectFailed0(WtPipeContext ctx, Throwable cause) {
		return true;
	}

	@Override
	protected boolean serverHandshakeFail0(WtPipeContext ctx, Throwable cause) {
		return true;
	}

}
