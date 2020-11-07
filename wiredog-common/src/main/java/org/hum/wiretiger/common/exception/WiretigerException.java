package org.hum.wiredog.common.exception;

public class wiredogException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public wiredogException(String msg) {
		super(msg);
	}

	public wiredogException(String msg, Throwable error) {
		super(msg, error);
	}
}
