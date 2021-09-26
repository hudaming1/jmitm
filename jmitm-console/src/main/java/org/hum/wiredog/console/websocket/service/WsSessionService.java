package org.hum.wiredog.console.websocket.service;

import org.hum.wiredog.console.common.Session;
import org.hum.wiredog.console.http.helper.ConsoleHelper;
import org.hum.wiredog.console.http.service.SessionService;
import org.hum.wiredog.console.http.vo.WiredogSessionListVO;
import org.hum.wiredog.console.websocket.ConsoleManager;
import org.hum.wiredog.console.websocket.bean.WsServerMessage;
import org.hum.wiredog.console.websocket.enumtype.MessageTypeEnum;
import org.hum.wiredog.proxy.facade.PipeContext;

public class WsSessionService {

	private static final ConsoleManager CM = ConsoleManager.get();

	public void sendNewSessionMsg(PipeContext pipe, Session session) {
		CM.getAll().forEach(channel -> {
			if (!SessionService.isMatch(session)) {
				return ;
			}
			WsServerMessage<WiredogSessionListVO> msg = new WsServerMessage<>(MessageTypeEnum.NewSession);
			msg.setData(ConsoleHelper.parse2WtSessionListVO(session));
			channel.writeAndFlush(msg);
		});
	}
	
}
