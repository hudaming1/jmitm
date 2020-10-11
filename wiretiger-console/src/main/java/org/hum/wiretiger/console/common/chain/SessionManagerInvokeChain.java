package org.hum.wiretiger.console.common.chain;

import java.util.Stack;

import org.hum.wiretiger.console.common.WtSession;
import org.hum.wiretiger.console.common.WtSessionManager;
import org.hum.wiretiger.console.websocket.service.WsSessionService;
import org.hum.wiretiger.proxy.facade.PipeInvokeChain;
import org.hum.wiretiger.proxy.facade.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.chain.DefaultPipeInvokeChain;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionManagerInvokeChain extends DefaultPipeInvokeChain {
	/**
	 * 保存了当前HTTP连接，正在等待响应的请求
	 */
	protected Stack<WtSession> reqStack4WattingResponse = new Stack<>();
	private static WsSessionService wsSessionService = new WsSessionService();

	public SessionManagerInvokeChain(PipeInvokeChain next) {
		super(next);
	}

	@Override
	protected boolean clientRead0(WtPipeContext ctx, FullHttpRequest request) {
		reqStack4WattingResponse.push(new WtSession(ctx.getId(), request, System.currentTimeMillis()));
		return true;
	}

	@Override
	protected boolean serverRead0(WtPipeContext ctx, FullHttpResponse response) {
		if (reqStack4WattingResponse.isEmpty() || reqStack4WattingResponse.size() > 1) {
			log.warn(this + "---reqStack4WattingResponse.size error, size=" + reqStack4WattingResponse.size());
			ctx.getClientChannel().writeAndFlush(response);
			return true;
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
		wsSessionService.sendNewSessionMsg(ctx, session);
		return true;
	}
}
