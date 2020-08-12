package org.hum.wiretiger.common.exception;

public class WireTigerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WireTigerException(String msg) {
		super(msg);
	}

	public WireTigerException(String msg, Throwable error) {
		super(msg, error);
	}
}
