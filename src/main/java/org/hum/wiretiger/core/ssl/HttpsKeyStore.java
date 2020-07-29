package org.hum.wiretiger.core.ssl;

import java.io.InputStream;

import org.hum.wiretiger.exception.WireTigerException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsKeyStore {

	public static InputStream getKeyStoreStream(String domain) {
		try {
			// 创建证书
			return CA_Station.create(domain);
		} catch (Exception e) {
			log.error("init key_store failure, domain=" + domain, e);
			throw new WireTigerException("init key store failed", e);
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
	public static String certificatePassword = "123456";
	public static String keystorePassword = "123456";
}
