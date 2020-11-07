//package org.hum.wiredog.console.common.listener;
//
//import org.hum.wiredog.console.websocket.service.WsPipeService;
//import org.hum.wiredog.console.websocket.service.WsSessionService;
//import org.hum.wiredog.proxy.facade.event.EventListener;
//import org.hum.wiredog.proxy.facade.event.wiredogPipe;
//import org.hum.wiredog.proxy.facade.event.wiredogSession;
//
//public class Console4WsListener implements EventListener {
//	
//	private WsPipeService wsPipeService = new WsPipeService();
//	private WsSessionService wsSessionService = new WsSessionService();
//
//	@Override
//	public void onConnect(wiredogPipe pipe) {
//		wsPipeService.sendConnectMsg(pipe);
//	}
//
//	@Override
//	public void onDisconnect(wiredogPipe pipe) {
//		wsPipeService.sendDisConnectMsg(pipe);
//	}
//
//	@Override
//	public void onError(wiredogPipe pipe) {
//		wsPipeService.sendStatusChangeMsg(pipe);
//	}
//
//	@Override
//	public void onPipeStatusChange(wiredogPipe pipe) {
//		wsPipeService.sendStatusChangeMsg(pipe);
//	}
//
//	@Override
//	public void onNewSession(wiredogPipe pipe, wiredogSession sessionReq) {
//		wsSessionService.sendNewSessionMsg(pipe, sessionReq);
//	}
//
//	@Override
//	public void onSessionUpdate(wiredogPipe pipe, wiredogSession sessionResp) {
//	}
//}
