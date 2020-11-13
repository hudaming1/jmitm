package org.hum.wiredog.proxy.pipe.chain;

import org.hum.wiredog.common.util.HttpMessageUtil.InetAddress;
import org.hum.wiredog.proxy.facade.PipeInvokeChain;
import org.hum.wiredog.proxy.facade.PipeContext;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class DefaultPipeInvokeChain extends PipeInvokeChain {

	public DefaultPipeInvokeChain(PipeInvokeChain next) {
		super(next);
	}

	@Override
	protected boolean clientConnect0(PipeContext ctx) {
		return true;
	}

	@Override
	protected boolean clientParsed0(PipeContext ctx) {
		return true;
	}

	@Override
	protected boolean clientRead0(PipeContext ctx, FullHttpRequest request) {
		return true;
	}

	@Override
	protected boolean serverConnect0(PipeContext ctx, InetAddress InetAddress) {
		return true;
	}

	@Override
	protected boolean serverHandshakeSucc0(PipeContext ctx) {
		return true;
	}

	@Override
	protected boolean serverFlush0(PipeContext ctx, FullHttpRequest request) {
		return true;
	}

	@Override
	protected boolean serverRead0(PipeContext ctx, FullHttpResponse response) {
		return true;
	}

	@Override
	protected boolean clientFlush0(PipeContext ctx, FullHttpResponse response) {
		return true;
	}

	@Override
	protected boolean clientClose0(PipeContext ctx) {
		return true;
	}

	@Override
	protected boolean serverClose0(PipeContext ctx) {
		return true;
	}

	@Override
	protected boolean clientError0(PipeContext ctx, Throwable cause) {
		return true;
	}

	@Override
	protected boolean serverError0(PipeContext ctx, Throwable cause) {
		return true;
	}

	@Override
	protected boolean clientHandshakeSucc0(PipeContext ctx) {
		return true;
	}

	@Override
	protected boolean clientHandshakeFail0(PipeContext ctx, Throwable cause) {
		return true;
	}

	@Override
	protected boolean serverConnectFailed0(PipeContext ctx, Throwable cause) {
		return true;
	}

	@Override
	protected boolean serverHandshakeFail0(PipeContext ctx, Throwable cause) {
		return true;
	}

}
