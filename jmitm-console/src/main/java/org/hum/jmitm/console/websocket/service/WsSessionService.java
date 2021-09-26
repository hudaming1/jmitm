package org.hum.jmitm.console.websocket.service;

import org.hum.jmitm.console.common.Session;
import org.hum.jmitm.console.http.helper.ConsoleHelper;
import org.hum.jmitm.console.http.service.SessionService;
import org.hum.jmitm.console.http.vo.WiredogSessionListVO;
import org.hum.jmitm.console.websocket.ConsoleManager;
import org.hum.jmitm.console.websocket.bean.WsServerMessage;
import org.hum.jmitm.console.websocket.enumtype.MessageTypeEnum;
import org.hum.jmitm.proxy.facade.PipeContext;

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
