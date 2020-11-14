package org.hum.wiredog.proxy.mock;

public enum MockStatus {

	Disabled(0, "禁用"),
	Enabled(1, "启用"),
	;

	private int code;
	private String name;
	
	MockStatus(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
	
	public static MockStatus getEnum(Integer code) {
		if (code == null) {
			return null;
		}
		for (MockStatus mockStatus : values()) {
			if (mockStatus.code == code) {
				return mockStatus;
			}
		}
		return null;
	}
}
