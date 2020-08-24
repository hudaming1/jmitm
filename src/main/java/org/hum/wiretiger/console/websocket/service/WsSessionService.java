package org.hum.wiretiger.console.websocket.service;

import org.hum.wiretiger.console.helper.PipeHolderHelper;
import org.hum.wiretiger.console.vo.WtPipeListVO;
import org.hum.wiretiger.console.websocket.ConsoleManager;
import org.hum.wiretiger.console.websocket.bean.WsServerMessage;
import org.hum.wiretiger.console.websocket.enumtype.MessageTypeEnum;
import org.hum.wiretiger.core.pipe.bean.PipeHolder;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class WsSessionService {

	private static final ConsoleManager CM = ConsoleManager.get();

	public void sendNewSessionMsg(PipeHolder pipe, DefaultHttpRequest sessionReq) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<WtPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.NewSession);
			msg.setData(PipeHolderHelper.parse(pipe));
			channel.writeAndFlush(msg);
		});
	}

	public void sendSessionUpdateMsg(PipeHolder pipe, FullHttpResponse sessionResp) {
		CM.getAll().forEach(channel -> {
			WsServerMessage<WtPipeListVO> msg = new WsServerMessage<>(MessageTypeEnum.SessionUpdate);
			msg.setData(PipeHolderHelper.parse(pipe));
			channel.writeAndFlush(msg);
		});
	}
	
}
