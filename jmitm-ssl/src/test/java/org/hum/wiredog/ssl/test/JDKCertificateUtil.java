package org.hum.wiredog.ssl.test;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

public class JDKCertificateUtil {
	public static final String SUBJECT = "C=CN,ST=HB,L=WH,O=HK,OU=CA,CN=";
	public static final String ISSUER = "C=CN,ST=HB,L=WH,O=HK,OU=CA,CN=CA";

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * 读取base64证书
	 * 
	 * @param cer
	 * @return
	 * @throws CertificateException
	 */
	public static X509Certificate readCert(String cer) throws CertificateException, IOException {
		CertificateFactory certificate_factory = CertificateFactory.getInstance("X.509");
		try (InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(cer))) {
			X509Certificate x509certificate = (X509Certificate) certificate_factory.generateCertificate(inputStream);
			return x509certificate;
		}
	}

	// Generate version 3 X509Certificate
	public static X509Certificate generateV3SelfSignedCertificate(Date startDate, Date endDate, PrivateKey rootPrik,
			PublicKey rootPubk, PublicKey pubk, String issuer, String subject) {
		try {
			X500Name issuerDN = new X500Name(issuer);
			X500Name subjectDN = new X500Name(subject);
			BigInteger serialNumber = BigInteger.valueOf(startDate.getTime());

			SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(pubk.getEncoded());
			// 证书对象构建
			X509v3CertificateBuilder builder = new X509v3CertificateBuilder(issuerDN, serialNumber, startDate, endDate,
					subjectDN, subPubKeyInfo);
			/*
			 * JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();
			 * GeneralName generalName = new
			 * GeneralName(GeneralName.dNSName,"www.111111.com"); GeneralName generalName2 =
			 * new GeneralName(GeneralName.iPAddress,"111.111.111.111"); GeneralNames
			 * generalNames = new GeneralNames(new GeneralName[]{generalName,generalName2});
			 * DistributionPointName distributionPoint =new
			 * DistributionPointName(generalNames); DistributionPoint[] points = new
			 * DistributionPoint[1]; points[0] = new
			 * DistributionPoint(distributionPoint,null,null); CRLDistPoint crlDistPoint =
			 * new CRLDistPoint(points);
			 */
			/*
			 * builder.addExtension(Extension.subjectAlternativeName,false,generalNames);
			 * builder.addExtension(Extension.authorityKeyIdentifier,false,
			 * extensionUtils.createAuthorityKeyIdentifier(rootPubk));
			 * builder.addExtension(Extension.subjectKeyIdentifier,false,extensionUtils.
			 * createAuthorityKeyIdentifier(pubk));
			 */
			// 证书完成数字签名
			X509CertificateHolder holder = builder.build(createSigner(rootPrik));
			return new JcaX509CertificateConverter().getCertificate(holder);
		} catch (Exception e) {
			throw new RuntimeException("Error creating X509v3Certificate.", e);
		}
	}

	/**
	 * 创建前面对象
	 * 
	 * @param privKey
	 * @return
	 * @throws OperatorCreationException
	 */
	public static ContentSigner createSigner(PrivateKey privKey) throws OperatorCreationException {
		return new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privKey);
	}

	/**
	 * 证书转换为PEM格式数字证书
	 */
	public static String convertToPem(X509Certificate x509Certificate)
			throws IOException, CertificateEncodingException {
		try (StringWriter str = new StringWriter(); JcaPEMWriter pemWriter = new JcaPEMWriter(str)) {
			PemObject pemObject = new PemObject("RSA CERTIFICATE", x509Certificate.getEncoded());
			pemWriter.writeObject(pemObject);
			pemWriter.flush();
			return str.toString();
		}
	}

	/**
	 * 证书转换为pfx格式证书,将结果集放到txt文件 扩展名pfx就可用于spring boot中作为https证书
	 */
	public static String convertToPfx(PrivateKey privateKey, X509Certificate[] x509Certificate) throws IOException,
			NoSuchProviderException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
		try (ByteArrayOutputStream fos = new ByteArrayOutputStream();) {
			KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
			keyStore.load(null, null);
			keyStore.setKeyEntry("123", privateKey, "123".toCharArray(), x509Certificate);
			keyStore.store(fos, "123".toCharArray());
			fos.flush();
			return Base64.getEncoder().encodeToString(fos.toByteArray());
		}
	}

	/**
	 * 双向ssl需要客户端配置证书和ca证书
	 * 
	 * @param caCert
	 * @param cert
	 * @param privateKey
	 * @param password
	 * @return
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 * @throws KeyManagementException
	 */
	public static SSLSocketFactory getSocketFactory(X509Certificate caCert, X509Certificate cert, PrivateKey privateKey,
			String password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException,
			UnrecoverableKeyException, KeyManagementException {

		// CA certificate is used to authenticate server
		KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
		caKs.load(null, null);
		caKs.setCertificateEntry("ca-certificate", caCert);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
		tmf.init(caKs);

		// client key and certificates are sent to server so it can authenticate
		// us
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, null);
		// ks.setCertificateEntry("certificate", cert);
		ks.setKeyEntry("private-key", privateKey, password.toCharArray(),
				new java.security.cert.Certificate[] { cert });
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, password.toCharArray());

		// finally, create SSL socket factory
		SSLContext context = SSLContext.getInstance("TLSv1.2");
//        SSLContext context = SSLContext.getInstance("SSL");
		context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		return context.getSocketFactory();
	}
}
