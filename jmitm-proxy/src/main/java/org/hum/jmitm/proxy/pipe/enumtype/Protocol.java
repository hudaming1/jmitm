package org.hum.jmitm.proxy.pipe.enumtype;

public enum Protocol {

	HTTP(1, "HTTP"),
	HTTPS(2, "HTTPS"),
	UNKNOW(0, "UNKNOW"),
	;

	Protocol(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	private int code;
	private String desc;

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
	
	public static Protocol getEnum(Integer code) {
		if (code == null) {
			return null;
		}
		for (Protocol protocol : values()) {
			if (protocol.code == code) {
				return protocol;
			}
		}
		return UNKNOW;
	}
}
