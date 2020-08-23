package org.hum.wiretiger.ws.listener;

import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.pipe.event.EventListener;
import org.hum.wiretiger.ws.service.WsPipeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WsListener implements EventListener {
	
	private WsPipeService wsPipeService = new WsPipeService();

	@Override
	public void onConnect(PipeHolder pipe) {
		wsPipeService.sendConnectMsg(pipe);
	}

	@Override
	public void onDisconnect(PipeHolder pipe) {
		log.info(pipe + " disconnect");
	}

	@Override
	public void onError(PipeHolder pipe) {
		log.info(pipe + " error");
	}
}
