package org.hum.wiretiger.ws.enumtype;

public enum MessageTypeEnum {

	PipeConnect(1001, "加入新的管道"),
	PipeDisconnect(1002, "管道断开连接"),
	PipeStatusChange(1003, "管道状态改变"),
	NewSession(2001, "创建新的回话"),
	
	
	Ping(9001, "Healthcheck"),
	Pong(9002, "Healthcheck"),
	
	MESSAGE_PARSE_ERROR(-1, "消息无法解析"),
	SYSTEM_ERROR(-9999, "系统异常"),
	;
	
	private int code;
	private String desc;
	
	MessageTypeEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
	
	public static MessageTypeEnum getEnum(Integer code) {
		if (code == null) {
			return null;
		}
		for (MessageTypeEnum typeEnum : values()) {
			if (typeEnum.code == code) {
				return typeEnum;
			}
		}
		return null;
	}
}
