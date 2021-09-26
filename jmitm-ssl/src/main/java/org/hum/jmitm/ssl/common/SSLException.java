package org.hum.jmitm.ssl.common;

public class SSLException  extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SSLException(String msg) {
		super(msg);
	}

	public SSLException(String msg, Throwable error) {
		super(msg, error);
	}
}
