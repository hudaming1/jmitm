package org.hum.wiretiger.ws.service;

import org.hum.wiretiger.core.pipe.bean.PipeHolder;
import org.hum.wiretiger.ws.ConsoleManager;
import org.hum.wiretiger.ws.bean.WsServerMessage;
import org.hum.wiretiger.ws.enumtype.MessageTypeEnum;

import com.alibaba.fastjson.JSONObject;

public class WsPipeService {

	private static final ConsoleManager CM = ConsoleManager.get();
	
	public void sendConnectMsg(PipeHolder pipeHolder) {
		CM.getAll().forEach(channel -> {
			System.out.println("aaaaaa");
			WsServerMessage msg = new WsServerMessage();
			msg.setType(MessageTypeEnum.PipeConnect);
			JSONObject json = new JSONObject();
			json.put("name", pipeHolder.getName());
			msg.setData(json);
			channel.writeAndFlush(msg);
		});
	}
}
