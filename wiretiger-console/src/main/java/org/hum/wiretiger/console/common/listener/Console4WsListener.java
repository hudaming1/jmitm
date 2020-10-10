//package org.hum.wiretiger.console.common.listener;
//
//import org.hum.wiretiger.console.websocket.service.WsPipeService;
//import org.hum.wiretiger.console.websocket.service.WsSessionService;
//import org.hum.wiretiger.proxy.facade.event.EventListener;
//import org.hum.wiretiger.proxy.facade.event.WiretigerPipe;
//import org.hum.wiretiger.proxy.facade.event.WiretigerSession;
//
//public class Console4WsListener implements EventListener {
//	
//	private WsPipeService wsPipeService = new WsPipeService();
//	private WsSessionService wsSessionService = new WsSessionService();
//
//	@Override
//	public void onConnect(WiretigerPipe pipe) {
//		wsPipeService.sendConnectMsg(pipe);
//	}
//
//	@Override
//	public void onDisconnect(WiretigerPipe pipe) {
//		wsPipeService.sendDisConnectMsg(pipe);
//	}
//
//	@Override
//	public void onError(WiretigerPipe pipe) {
//		wsPipeService.sendStatusChangeMsg(pipe);
//	}
//
//	@Override
//	public void onPipeStatusChange(WiretigerPipe pipe) {
//		wsPipeService.sendStatusChangeMsg(pipe);
//	}
//
//	@Override
//	public void onNewSession(WiretigerPipe pipe, WiretigerSession sessionReq) {
//		wsSessionService.sendNewSessionMsg(pipe, sessionReq);
//	}
//
//	@Override
//	public void onSessionUpdate(WiretigerPipe pipe, WiretigerSession sessionResp) {
//	}
//}
