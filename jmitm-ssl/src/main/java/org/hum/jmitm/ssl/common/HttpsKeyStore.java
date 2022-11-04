package org.hum.jmitm.ssl.common;

import java.io.InputStream;

import org.hum.jmitm.ssl.CA_Station;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsKeyStore {
	
	private static final char[] APP_CERT_PASS = "".toCharArray();
	private static final char[] APP_KEY_PASS = "".toCharArray();

	public static InputStream getKeyStoreStream(String domain) {
		try {
			// 创建证书
			return CA_Station.createWithCache(domain);
		} catch (Exception e) {
			log.error("init key_store failure, domain=" + domain, e);
			throw new SSLException("init key store failed", e);
		}
	}

	public static char[] getCertificatePassword() {
		return APP_CERT_PASS;
	}

	public static char[] getKeyStorePassword() {
		return APP_KEY_PASS;
	}
}
