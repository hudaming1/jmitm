package org.hum.wiretiger.ws.bean;

import org.hum.wiretiger.ws.enumtype.MessageTypeEnum;

import com.alibaba.fastjson.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsServerMessage {

	// 类型
	private MessageTypeEnum type;
	// 消息内容
	private JSONObject data;
	
	public WsServerMessage(MessageTypeEnum type) {
		this.type = type;
	}
}
