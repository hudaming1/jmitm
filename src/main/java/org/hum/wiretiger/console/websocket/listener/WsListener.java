package org.hum.wiretiger.console.websocket.listener;

import org.hum.wiretiger.console.websocket.service.WsPipeService;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.pipe.event.EventListener;

public class WsListener implements EventListener {
	
	private WsPipeService wsPipeService = new WsPipeService();

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
}
