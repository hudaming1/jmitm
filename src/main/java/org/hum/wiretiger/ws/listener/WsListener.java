package org.hum.wiretiger.ws.listener;

import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.core.pipe.event.EventListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WsListener implements EventListener {

	@Override
	public void onConnect(PipeHolder pipe) {
		log.info(pipe.getClientChannel().remoteAddress() + " connect");
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
