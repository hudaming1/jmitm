package org.hum.wiretiger.console.common.chain;

import java.util.Stack;

import org.hum.wiretiger.console.common.WtSessionManager;
import org.hum.wiretiger.proxy.facade.PipeInvokeChain;
import org.hum.wiretiger.proxy.facade.WtPipeContext;
import org.hum.wiretiger.proxy.pipe.chain.DefaultPipeInvokeChain;
import org.hum.wiretiger.proxy.session.bean.WtSession;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionManagerInvokeChain extends DefaultPipeInvokeChain {
	/**
	 * 保存了当前HTTP连接，正在等待响应的请求
	 */
	protected Stack<WtSession> reqStack4WattingResponse = new Stack<>();

	public SessionManagerInvokeChain(PipeInvokeChain next) {
		super(next);
	}

	@Override
	protected void clientRead0(WtPipeContext ctx, FullHttpRequest request) {
		reqStack4WattingResponse.push(new WtSession(ctx.getId(), request, System.currentTimeMillis()));
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
}
