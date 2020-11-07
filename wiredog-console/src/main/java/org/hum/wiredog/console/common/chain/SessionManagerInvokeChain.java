package org.hum.wiredog.console.common.chain;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hum.wiredog.common.util.SyncLinkedHashMap;
import org.hum.wiredog.console.common.WtSession;
import org.hum.wiredog.console.websocket.service.WsSessionService;
import org.hum.wiredog.proxy.facade.PipeInvokeChain;
import org.hum.wiredog.proxy.facade.WtPipeContext;
import org.hum.wiredog.proxy.pipe.chain.DefaultPipeInvokeChain;

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
	private static Map<Long, WtSession> RequestIndex4Id;
	private static final AtomicBoolean RequestIndex4Id_INIT_FLAG = new AtomicBoolean();

	public SessionManagerInvokeChain(PipeInvokeChain next, int sessionHistory) {
		super(next);
		initRequsetMap(sessionHistory);
	}
	
	private void initRequsetMap(int size) {
		if (RequestIndex4Id_INIT_FLAG.compareAndSet(false, true)) {
			RequestIndex4Id = new SyncLinkedHashMap<>(size);
		}
	}

	@Override
	protected boolean clientRead0(WtPipeContext ctx, FullHttpRequest request) {
		WtSession session = new WtSession(ctx.getId(), request, System.currentTimeMillis());
		byte[] bytes = null;
		if (request.content().readableBytes() > 0) {
			bytes = new byte[request.content().readableBytes()];
			request.content().duplicate().readBytes(bytes);
		}
		session.setRequestBytes(bytes);
		reqStack4WattingResponse.push(session);
		RequestIndex4Id.put(session.getId(), session);
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
		wsSessionService.sendNewSessionMsg(ctx, session);
		return true;
	}
	
	public static Collection<WtSession> getAll() {
		if (RequestIndex4Id == null || RequestIndex4Id.values() == null) {
			return Collections.unmodifiableCollection(Collections.emptyList());
		}
		return Collections.unmodifiableCollection(RequestIndex4Id.values());
	}
	
	public static void clearAll() {
		if (RequestIndex4Id == null || RequestIndex4Id.isEmpty()) {
			return ;
		}
		RequestIndex4Id.clear();
	}
	
	public static WtSession getById(long id) {
		return RequestIndex4Id.get(id);
	}
	
	public static void addSession(WtSession session) {
		RequestIndex4Id.put(session.getId(), session);
		wsSessionService.sendNewSessionMsg(null, session);
	}
}
