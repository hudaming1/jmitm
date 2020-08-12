package org.hum.wiretiger.core.ssl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.Extension;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import sun.security.x509.GeneralNames;

public class CA_Creator implements Callable<byte[]> {
	
	private static final String CERT_ALIAS = "wire_tiger";
	private static final String CA_ALIAS = "1";
	private static final String CA_PASS = "wiretiger@123";
	private static final String CA_FILE = CA_Station.class.getResource("/cert/server.p12").getFile();
	private String domain;
	
	public CA_Creator(String domain) {
		this.domain = domain;
	}

	private static byte[] _create(String domain) throws Exception {
		// 读取CA证书的JKS文件
		KeyStore caStore = KeyStore.getInstance("PKCS12");
		File caFile = new File(CA_FILE);
		caStore.load(new FileInputStream(caFile), CA_PASS.toCharArray());

		// 给alice签发证书并存为server_cert.p12的文件
		PrivateKeyEntry caPrivateKey = (PrivateKeyEntry) caStore.getEntry(CA_ALIAS, new PasswordProtection(CA_PASS.toCharArray()));
		String serverSubject = "CN=" + domain;
		ByteArrayOutputStream baos = gen(domain, caPrivateKey, serverSubject, CERT_ALIAS);
		byte[] bytes = baos.toByteArray();
		baos.close();
		return bytes;
	}

	// 用KeyEntry形式存储一个私钥以及对应的证书，并把CA证书加入到它的信任证书列表里面。
	public static ByteArrayOutputStream store(PrivateKey key, Certificate cert, Certificate caCert, String name)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore store = KeyStore.getInstance("PKCS12");
		store.load(null, null);
		store.setKeyEntry(name, key, CA_PASS.toCharArray(), new Certificate[] { cert });
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		store.store(baos, CA_PASS.toCharArray());
		return baos;
	}

	// 用ke所代表的CA给subject签发证书，并存储到名称为name的jks文件里面。
	public static ByteArrayOutputStream gen(String domain, PrivateKeyEntry caPrivateKey, String serverSubject, String name) throws Exception {
		sun.security.x509.X509CertImpl caCert = (sun.security.x509.X509CertImpl) caPrivateKey.getCertificate();
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair keyPair = kpg.generateKeyPair();

		KeyStore store = KeyStore.getInstance("PKCS12");
		store.load(null, null);

		sun.security.x509.X500Name caX500Name = (sun.security.x509.X500Name) caCert.getSubjectDN();
		// 这里取了rfc2253，下面用的是rfc4519，两者格式能兼容？
		String issuer = caX500Name.getRFC2253Name();
		//
		List<Extension> extensions = new ArrayList<>();
		sun.security.x509.SubjectAlternativeNameExtension ext = new sun.security.x509.SubjectAlternativeNameExtension();
		GeneralNames names = new GeneralNames();
		names.add(new sun.security.x509.GeneralName(new UnsafeDNSName(domain)));
		ext.set("subject_name", names);
		extensions.add(ext);
		// 这个序列号要动态生成
		Certificate serverCert = generateV3(issuer, serverSubject, new BigInteger(System.currentTimeMillis() + ""),
				new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24),
				new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 32), keyPair.getPublic(), // 待签名的公钥
				caPrivateKey.getPrivateKey()// CA的私钥
				, extensions);
		return store(keyPair.getPrivate(), serverCert, caPrivateKey.getCertificate(), name);
	}

	public static Certificate generateV3(String issuer, String subject, BigInteger serial, Date notBefore,
			Date notAfter, PublicKey publicKey, PrivateKey privKey, List<Extension> extensions)
			throws OperatorCreationException, CertificateException, IOException {
		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(new X500Name(RFC4519Style.INSTANCE, issuer),
				serial, notBefore, notAfter, new X500Name(subject), publicKey);
		// 这里不要使用SHA1算法，Chrome浏览器会提示「NET::ERR_CERT_WEAK_SIGNATURE_ALGORITHM」意为使用了过期的加密算法
		ContentSigner sigGen = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privKey);
		// privKey是CA的私钥，publicKey是待签名的公钥，那么生成的证书就是被CA签名的证书。
		if (extensions != null) {
			for (Extension ext : extensions) {
				builder.addExtension(new ASN1ObjectIdentifier(ext.getId()), ext.isCritical(),
						ASN1Primitive.fromByteArray(ext.getValue()));
			}
		}
		X509CertificateHolder holder = builder.build(sigGen);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream is1 = new ByteArrayInputStream(holder.toASN1Structure().getEncoded());
		X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);
		is1.close();
		return theCert;
	}
	
	@Override
	public byte[] call() throws Exception {
		return _create(domain);
	}
}
