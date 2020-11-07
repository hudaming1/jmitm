package org.hum.wiredog.common.exception;

public class WiredogException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WiredogException(String msg) {
		super(msg);
	}

	public WiredogException(String msg, Throwable error) {
		super(msg, error);
	}
}
