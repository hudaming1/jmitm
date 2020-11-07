package org.hum.wiredog.console.websocket.service;

import org.hum.wiredog.console.http.helper.ConsoleHelper;
import org.hum.wiredog.console.http.vo.wiredogPipeListVO;
import org.hum.wiredog.console.websocket.ConsoleManager;
import org.hum.wiredog.console.websocket.bean.WsServerMessage;
import org.hum.wiredog.console.websocket.enumtype.MessageTypeEnum;
import org.hum.wiredog.proxy.facade.WtPipeContext;

public class WsPipeService {

	private static final ConsoleManager CM = ConsoleManager.get();
	
	public void sendConnectMsg(WtPipeContext ctx) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<wiredogPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.PipeConnect);
			msg.setData(ConsoleHelper.parse2WtPipeListVO(ctx));
			channel.writeAndFlush(msg);
		});
	}

	public void sendStatusChangeMsg(WtPipeContext ctx) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<wiredogPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.PipeUpdate);
			msg.setData(ConsoleHelper.parse2WtPipeListVO(ctx));
			channel.writeAndFlush(msg);
		});
	}

	public void sendDisConnectMsg(WtPipeContext ctx) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<wiredogPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.PipeDisconnect);
			msg.setData(ConsoleHelper.parse2WtPipeListVO(ctx));
			channel.writeAndFlush(msg);
		});
	}
}
