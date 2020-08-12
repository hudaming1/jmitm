package org.hum.wiretiger.common.exception;

public class WiretigerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WiretigerException(String msg) {
		super(msg);
	}

	public WiretigerException(String msg, Throwable error) {
		super(msg, error);
	}
}
