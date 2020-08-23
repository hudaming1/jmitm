package org.hum.wiretiger.console.websocket.bean;

import org.hum.wiretiger.console.websocket.enumtype.MessageTypeEnum;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

@Data
public class WsClientMessage {

	// 消息类型
	private MessageTypeEnum type;
	// 消息内容
	private JSONObject data;
}
