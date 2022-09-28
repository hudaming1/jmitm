package org.hum.jmitm.ssl.common;

import java.io.InputStream;

import org.hum.jmitm.ssl.CA_Station;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsKeyStore {

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
		return Arguments.certificatePassword.toCharArray();
	}

	public static char[] getKeyStorePassword() {
		return Arguments.keystorePassword.toCharArray();
	}
}

class Arguments {
	public static String certificatePassword = "jmitm@123";
	public static String keystorePassword = "jmitm@123";
}
