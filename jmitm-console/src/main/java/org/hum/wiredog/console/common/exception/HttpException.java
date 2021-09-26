package org.hum.wiredog.console.common.exception;

public class HttpException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public HttpException(String msg) {
		super(msg);
	}

	public HttpException(String msg, Throwable error) {
		super(msg, error);
	}
}
