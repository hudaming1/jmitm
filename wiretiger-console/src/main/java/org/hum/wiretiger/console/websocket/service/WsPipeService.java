package org.hum.wiretiger.console.websocket.service;

import org.hum.wiretiger.console.http.helper.ConsoleHelper;
import org.hum.wiretiger.console.http.vo.WtPipeListVO;
import org.hum.wiretiger.console.websocket.ConsoleManager;
import org.hum.wiretiger.console.websocket.bean.WsServerMessage;
import org.hum.wiretiger.console.websocket.enumtype.MessageTypeEnum;
import org.hum.wiretiger.proxy.facade.event.WiretigerPipe;

public class WsPipeService {

	private static final ConsoleManager CM = ConsoleManager.get();
	
	public void sendConnectMsg(WiretigerPipe pipeHolder) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<WtPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.PipeConnect);
			msg.setData(ConsoleHelper.parse2WtPipeListVO(pipeHolder));
			channel.writeAndFlush(msg);
		});
	}

	public void sendStatusChangeMsg(WiretigerPipe pipe) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<WtPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.PipeUpdate);
			msg.setData(ConsoleHelper.parse2WtPipeListVO(pipe));
			channel.writeAndFlush(msg);
		});
	}

	public void sendDisConnectMsg(WiretigerPipe pipe) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<WtPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.PipeDisconnect);
			msg.setData(ConsoleHelper.parse2WtPipeListVO(pipe));
			channel.writeAndFlush(msg);
		});
	}
}
