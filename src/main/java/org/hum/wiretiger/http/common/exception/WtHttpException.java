package org.hum.wiretiger.http.common.exception;

public class WtHttpException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WtHttpException(String msg) {
		super(msg);
	}

	public WtHttpException(String msg, Throwable error) {
		super(msg, error);
	}
}
