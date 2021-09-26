package org.hum.jmitm.console.common.listener;
//package org.hum.jmitm.console.common.listener;
//
//import org.hum.jmitm.console.websocket.service.WsPipeService;
//import org.hum.jmitm.console.websocket.service.WsSessionService;
//import org.hum.jmitm.proxy.facade.event.EventListener;
//import org.hum.jmitm.proxy.facade.event.wiredogPipe;
//import org.hum.jmitm.proxy.facade.event.wiredogSession;
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
