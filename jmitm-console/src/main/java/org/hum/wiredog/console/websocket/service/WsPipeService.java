package org.hum.wiredog.console.websocket.service;

import org.hum.wiredog.console.http.helper.ConsoleHelper;
import org.hum.wiredog.console.http.vo.WiredogPipeListVO;
import org.hum.wiredog.console.websocket.ConsoleManager;
import org.hum.wiredog.console.websocket.bean.WsServerMessage;
import org.hum.wiredog.console.websocket.enumtype.MessageTypeEnum;
import org.hum.wiredog.proxy.facade.PipeContext;

public class WsPipeService {

	private static final ConsoleManager CM = ConsoleManager.get();
	
	public void sendConnectMsg(PipeContext ctx) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<WiredogPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.PipeConnect);
			msg.setData(ConsoleHelper.parse2WtPipeListVO(ctx));
			channel.writeAndFlush(msg);
		});
	}

	public void sendStatusChangeMsg(PipeContext ctx) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<WiredogPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.PipeUpdate);
			msg.setData(ConsoleHelper.parse2WtPipeListVO(ctx));
			channel.writeAndFlush(msg);
		});
	}

	public void sendDisConnectMsg(PipeContext ctx) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<WiredogPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.PipeDisconnect);
			msg.setData(ConsoleHelper.parse2WtPipeListVO(ctx));
			channel.writeAndFlush(msg);
		});
	}
}
