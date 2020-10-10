package org.hum.wiretiger.proxy.session;

import java.util.Stack;

import org.hum.wiretiger.proxy.pipe.bean.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.chain.FullPipeHandler;
import org.hum.wiretiger.proxy.session.bean.WtSession;
import org.hum.wiretiger.proxy.util.HttpMessageUtil.InetAddress;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionManagerHandler extends FullPipeHandler {
	/**
	 * 保存了当前HTTP连接，没有等待响应的请求
	 */
	protected Stack<WtSession> reqStack4WattingResponse = new Stack<>();

	public SessionManagerHandler(FullPipeHandler next) {
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
		reqStack4WattingResponse.push(new WtSession(ctx.getId(), request, System.currentTimeMillis()));
	}

	@Override
	protected void serverConnect0(WtPipeContext ctx, InetAddress inetAddress) {
	}

	@Override
	protected void serverHandshakeSucc0(WtPipeContext ctx) {
	}

	@Override
	protected void serverRead0(WtPipeContext ctx, FullHttpResponse response) {
		if (reqStack4WattingResponse.isEmpty() || reqStack4WattingResponse.size() > 1) {
			log.warn(this + "---reqStack4WattingResponse.size error, size=" + reqStack4WattingResponse.size());
			ctx.getClientChannel().writeAndFlush(response);
			return ;
		}
		WtSession session = reqStack4WattingResponse.pop();

		byte[] bytes = null;
		if (response.content().readableBytes() > 0) {
			bytes = new byte[response.content().readableBytes()];
			response.content().duplicate().readBytes(bytes);
		}
		
		session.setResponse(response, bytes, System.currentTimeMillis());
		// TODO 目前是当服务端返回结果，具备构建一个完整当Session后才触发NewSession事件，后续需要将动作置前
		WtSessionManager.get().add(session);
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

	@Override
	protected void serverFlush0(WtPipeContext ctx, FullHttpRequest request) {
	}

}
