package org.hum.wiretiger.console.websocket.bean;

import org.hum.wiretiger.console.websocket.enumtype.MessageTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsServerMessage<T> {

	/**
	 * 类型
	 * {@link MessageTypeEnum}
	 */
	private int type;
	// 消息内容
	private T data;
	
	public WsServerMessage(MessageTypeEnum type) {
		this.type = type.getCode();
	}
}
