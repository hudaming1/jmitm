package org.hum.wiredog.ssl.test;

import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.hum.jmitm.ssl.common.CertUtils;

public class GetCertTest {


	public static void main(String[] args) throws Exception {
		X509Certificate cert = CertUtils.getCert("www.baidu.com");

		System.out.println("   Subject " + cert.getSubjectDN());
		System.out.println("   Issuer  " + cert.getIssuerDN());
		
		if (cert.getNonCriticalExtensionOIDs() != null) {
			for (String extId : cert.getNonCriticalExtensionOIDs()) {
				System.out.println("   extension " + extId + "\t:" + Arrays.toString(cert.getExtensionValue(extId)));
			}
		}
		if (cert.getExtendedKeyUsage() != null) {
			for (String extId : cert.getExtendedKeyUsage()) {
				System.out.println("   extension " + extId + "\t:" + Arrays.toString(cert.getExtensionValue(extId)));
			}
		}
		
		System.out.println(toHexString(cert.getExtensionValue("2.5.29.17")));
	}

	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	private static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int b : bytes) {
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(' ');
		}
		return sb.toString();
	}
}
