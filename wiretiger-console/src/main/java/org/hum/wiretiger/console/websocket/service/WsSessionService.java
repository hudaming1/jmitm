package org.hum.wiretiger.console.websocket.service;

import org.hum.wiretiger.console.http.helper.ConsoleHelper;
import org.hum.wiretiger.console.http.vo.WiretigerSessionListVO;
import org.hum.wiretiger.console.websocket.ConsoleManager;
import org.hum.wiretiger.console.websocket.bean.WsServerMessage;
import org.hum.wiretiger.console.websocket.enumtype.MessageTypeEnum;
import org.hum.wiretiger.proxy.facade.event.WiretigerPipe;
import org.hum.wiretiger.proxy.facade.event.WiretigerSession;

public class WsSessionService {

	private static final ConsoleManager CM = ConsoleManager.get();

	public void sendNewSessionMsg(WiretigerPipe pipe, WiretigerSession session) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<WiretigerSessionListVO> msg = new WsServerMessage<>(MessageTypeEnum.NewSession);
			msg.setData(ConsoleHelper.parse2WtSessionListVO(session));
			channel.writeAndFlush(msg);
		});
	}
	
	
}
