package org.hum.wiretiger.console.websocket.listener;

import org.hum.wiretiger.console.websocket.service.WsPipeService;
import org.hum.wiretiger.console.websocket.service.WsSessionService;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.pipe.event.EventListener;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class Console4WsListener implements EventListener {
	
	private WsPipeService wsPipeService = new WsPipeService();
	private WsSessionService wsSessionService = new WsSessionService();

	@Override
	public void onConnect(PipeHolder pipe) {
		wsPipeService.sendConnectMsg(pipe);
	}

	@Override
	public void onDisconnect(PipeHolder pipe) {
		wsPipeService.sendDisConnectMsg(pipe);
	}

	@Override
	public void onError(PipeHolder pipe) {
		wsPipeService.sendStatusChangeMsg(pipe);
	}

	@Override
	public void onPipeStatusChange(PipeHolder pipe) {
		wsPipeService.sendStatusChangeMsg(pipe);
	}

	@Override
	public void onNewSession(PipeHolder pipe, DefaultHttpRequest sessionReq) {
		wsSessionService.sendNewSessionMsg(pipe, sessionReq);
	}

	@Override
	public void onSessionUpdate(PipeHolder pipe, FullHttpResponse sessionResp) {
		wsSessionService.sendSessionUpdateMsg(pipe, sessionResp);
	}
}
