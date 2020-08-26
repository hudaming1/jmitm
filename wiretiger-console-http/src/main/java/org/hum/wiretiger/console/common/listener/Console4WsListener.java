package org.hum.wiretiger.console.common.listener;

import org.hum.wiretiger.console.websocket.service.WsPipeService;
import org.hum.wiretiger.console.websocket.service.WsSessionService;
import org.hum.wiretiger.facade.proxy.EventListener;
import org.hum.wiretiger.facade.proxy.WireTigerPipe;
import org.hum.wiretiger.facade.proxy.WiretigerSession;

public class Console4WsListener implements EventListener {
	
	private WsPipeService wsPipeService = new WsPipeService();
	private WsSessionService wsSessionService = new WsSessionService();

	@Override
	public void onConnect(WireTigerPipe pipe) {
		wsPipeService.sendConnectMsg(pipe);
	}

	@Override
	public void onDisconnect(WireTigerPipe pipe) {
		wsPipeService.sendDisConnectMsg(pipe);
	}

	@Override
	public void onError(WireTigerPipe pipe) {
		wsPipeService.sendStatusChangeMsg(pipe);
	}

	@Override
	public void onPipeStatusChange(WireTigerPipe pipe) {
		wsPipeService.sendStatusChangeMsg(pipe);
	}

	@Override
	public void onNewSession(WireTigerPipe pipe, WiretigerSession sessionReq) {
		wsSessionService.sendNewSessionMsg(pipe, sessionReq);
	}

	@Override
	public void onSessionUpdate(WireTigerPipe pipe, WiretigerSession sessionResp) {
	}
}
