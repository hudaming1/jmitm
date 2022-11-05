package org.hum.wiredog.ssl.test;

import java.io.FileInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class Test222 {
	public static void main(String[] args) throws Exception {
		/* 取出证书--从文件中取出 */
		String certpath = "/Users/hudaming/Downloads/58-com.pem";
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		FileInputStream in1 = new FileInputStream(certpath);
		Certificate c = cf.generateCertificate(in1);
		X509Certificate x509Cert = (X509Certificate) c;

		// JAVA程序中显示证书指定信息
		System.out.println("输出证书信息:" + c.toString());
		System.out.println("版本号:" + x509Cert.getVersion());
		System.out.println("序列号:" + x509Cert.getSerialNumber().toString(16));
		System.out.println("主体名：" + x509Cert.getSubjectDN());
		System.out.println("签发者：" + x509Cert.getIssuerDN());
		System.out.println("有效期：" + x509Cert.getNotBefore());
		System.out.println("签名算法：" + x509Cert.getSigAlgName());
		byte[] sig = x509Cert.getSignature();// 签名值
		System.out.println("签名值：" + Arrays.toString(sig));
		PublicKey pk = x509Cert.getPublicKey();
		byte[] pkenc = pk.getEncoded();
		System.out.println("公钥");
		for (byte b : pkenc)
			System.out.print(b + ",");
		
		// DER:04:82:01:68:01:66:00:75:00:e8:3e:d0:da:3e:f5:06:35:32:e7:57:28:bc:89:6b:c9:03:d3:cb:d1:11:6b:ec:eb:69:e1:77:7d:6d:06:bd:6e:00:00:01:7f:29:a3:b3:fa:00:00:04:03:00:46:30:44:02:20:20:b2:8a:38:e2:23:ad:f8:89:1c:20:b9:fc:07:0e:48:09:ba:42:a2:bd:4b:72:84:ea:14:c9:6f:59:0f:b2:f0:02:20:33:25:e0:18:63:84:20:40:ce:9a:59:4d:30:c1:ac:ee:1f:89:0b:9e:18:9a:69:ce:6c:03:10:40:f2:e6:28:78:00:76:00:6f:53:76:ac:31:f0:31:19:d8:99:00:a4:51:15:ff:77:15:1c:11:d9:02:c1:00:29:06:8d:b2:08:9a:37:d9:13:00:00:01:7f:29:a3:b2:e6:00:00:04:03:00:47:30:45:02:21:00:d5:66:ae:a1:60:fd:4c:84:0a:c2:c3:ac:76:16:73:a7:3b:94:38:66:da:bb:b3:a6:dc:a4:39:19:20:1e:46:8f:02:20:57:c2:01:b0:e1:e4:d1:b5:c0:53:df:89:13:b1:42:12:90:12:4b:80:ea:38:d9:81:d4:ea:26:cd:39:89:23:0c:00:75:00:55:81:d4:c2:16:90:36:01:4a:ea:0b:9b:57:3c:53:f0:c0:e4:38:78:70:25:08:17:2f:a3:aa:1d:07:13:d3:0c:00:00:01:7f:29:a3:b4:1b:00:00:04:03:00:46:30:44:02:20:30:fb:39:01:d1:5a:8f:70:00:b1:73:01:c5:a0:62:2b:99:50:ce:d8:36:dc:95:14:b9:a6:d7:a2:5e:e1:0e:56:02:20:21:f3:c1:e6:52:e8:9d:89:82:f8:1f:a3:c6:f4:cc:c2:45:da:5c:30:8b:0d:5f:ee:17:ee:2b:8a:8f:37:b1:c9
		
		
		// E8:3E:D0:DA:3E:F5:06:35:32:E7:57:28:BC:89:6B:C9:03:D3:CB:D1:11:6B:EC:EB:69:E1:77:7D:6D:06:BD:6E
		
		byte[] sctBytes = new byte[] { 4, -126, 1, 104, 1, 102, 0, 117, 0, -24, 62, -48, -38, 62, -11, 6, 53, 50, -25, 87, 40, -68, -119, 107, -55, 3, -45, -53, -47, 17, 107, -20, -21, 105, -31, 119, 125, 109, 6, -67, 110, 0, 0, 1, 127, 41, -93, -77, -6, 0, 0, 4, 3, 0, 70, 48, 68, 2, 32, 32, -78, -118, 56, -30, 35, -83, -8, -119, 28, 32, -71, -4, 7, 14, 72, 9, -70, 66, -94, -67, 75, 114, -124, -22, 20, -55, 111, 89, 15, -78, -16, 2, 32, 51, 37, -32, 24, 99, -124, 32, 64, -50, -102, 89, 77, 48, -63, -84, -18, 31, -119, 11, -98, 24, -102, 105, -50, 108, 3, 16, 64, -14, -26, 40, 120, 0, 118, 0, 111, 83, 118, -84, 49, -16, 49, 25, -40, -103, 0, -92, 81, 21, -1, 119, 21, 28, 17, -39, 2, -63, 0, 41, 6, -115, -78, 8, -102, 55, -39, 19, 0, 0, 1, 127, 41, -93, -78, -26, 0, 0, 4, 3, 0, 71, 48, 69, 2, 33, 0, -43, 102, -82, -95, 96, -3, 76, -124, 10, -62, -61, -84, 118, 22, 115, -89, 59, -108, 56, 102, -38, -69, -77, -90, -36, -92, 57, 25, 32, 30, 70, -113, 2, 32, 87, -62, 1, -80, -31, -28, -47, -75, -64, 83, -33, -119, 19, -79, 66, 18, -112, 18, 75, -128, -22, 56, -39, -127, -44, -22, 38, -51, 57, -119, 35, 12, 0, 117, 0, 85, -127, -44, -62, 22, -112, 54, 1, 74, -22, 11, -101, 87, 60, 83, -16, -64, -28, 56, 120, 112, 37, 8, 23, 47, -93, -86, 29, 7, 19, -45, 12, 0, 0, 1, 127, 41, -93, -76, 27, 0, 0, 4, 3, 0, 70, 48, 68, 2, 32, 48, -5, 57, 1, -47, 90, -113, 112, 0, -79, 115, 1, -59, -96, 98, 43, -103, 80, -50, -40, 54, -36, -107, 20, -71, -90, -41, -94, 94, -31, 14, 86, 2, 32, 33, -13, -63, -26, 82, -24, -99, -119, -126, -8, 31, -93, -58, -12, -52, -62, 69, -38, 92, 48, -117, 13, 95, -18, 23, -18, 43, -118, -113, 55, -79, -55 };
		System.out.println();
	}
}
