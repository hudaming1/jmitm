package org.hum.jmitm.console.websocket.service;

import org.hum.jmitm.console.http.helper.ConsoleHelper;
import org.hum.jmitm.console.http.vo.WiredogPipeListVO;
import org.hum.jmitm.console.websocket.ConsoleManager;
import org.hum.jmitm.console.websocket.bean.WsServerMessage;
import org.hum.jmitm.console.websocket.enumtype.MessageTypeEnum;
import org.hum.jmitm.proxy.facade.PipeContext;

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
