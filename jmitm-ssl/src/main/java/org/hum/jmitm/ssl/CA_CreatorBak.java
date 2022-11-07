package org.hum.jmitm.ssl;

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
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.Extension;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.hum.jmitm.ssl.common.HttpsKeyStore;

import lombok.extern.slf4j.Slf4j;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.GeneralNames;

@Slf4j
@SuppressWarnings("restriction")
public class CA_CreatorBak implements Callable<byte[]> {
	
	private static final String CERT_ALIAS = "wire_tiger";
	private static final String CA_ALIAS = "1";
	private static final String CA_PASS = "wiretiger@123";
	// CA文件（里面包含了私钥和机构信息，这个私钥对应的公钥CA已经种到了客户端）
	private static final String CA_FILE = CA_Station.class.getResource("/cert/server.p12").getFile();
	
	static {
		try {
			Security.addProvider(new BouncyCastleProvider());
			log.info("finish init BC");
		} catch (Exception e) {
			log.error("DefaultwiredogServer init BC error", e);
			System.exit(-1);
		}
	}
	private String domain;
	
	public CA_CreatorBak(String domain) {
		this.domain = domain;
	}

	private static byte[] _create(String domain) throws Exception {
		// 读取CA证书的JKS文件 （这里应该可以缓存起来，因为CA是一直不变的）
		KeyStore caStore = KeyStore.getInstance("PKCS12");
		caStore.load(new FileInputStream(new File(CA_FILE)), CA_PASS.toCharArray());

		// 根据CA的路径，从文件中读取出CA的私钥信息
		PrivateKeyEntry caPrivateKey = (PrivateKeyEntry) caStore.getEntry(CA_ALIAS, new PasswordProtection(CA_PASS.toCharArray()));
		
		
		// 有了CA的私钥，和要签发证书的域名，我们就可以创建一个证书请求并用私钥签发
		ByteArrayOutputStream baos = generateAppCert(domain, caPrivateKey);
		byte[] bytes = baos.toByteArray();
		baos.close();
		return bytes;
	}

	// 用ke所代表的CA给subject签发证书，并存储到名称为name的jks文件里面。
	
	private static ByteArrayOutputStream generateAppCert(String domain, PrivateKeyEntry caPrivateKey) throws Exception {

		sun.security.x509.X509CertImpl caCert = (sun.security.x509.X509CertImpl) caPrivateKey.getCertificate();
		
		// 生成一组非对称加密键值对，后续作为动态网站证书的公钥和私钥
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair keyPair = kpg.generateKeyPair();

		sun.security.x509.X500Name caX500Name = (sun.security.x509.X500Name) caCert.getSubjectDN();
		// 这里取了rfc2253，下面用的是rfc4519，两者格式能兼容？
		String issuer = caX500Name.getRFC2253Name();
		
		//
		List<Extension> extensions = new ArrayList<>();
		
		// 证书名称
		GeneralNames names = new GeneralNames();
		names.add(new sun.security.x509.GeneralName(new UnsafeDNSName(domain)));
		
		// 因为不同浏览器在对证书校验时，采用的规范有所不同，所以我们将一些常见的字段都赋上值
		extensions.add(new sun.security.x509.SubjectAlternativeNameExtension(names));
		extensions.add(new sun.security.x509.BasicConstraintsExtension(false, 0));
		// keyUsageExt : digitalSignature && keyEncipherment - > 10100000 
//		extensions.add(new sun.security.x509.KeyUsageExtension(false, new byte[] { 1, 0, 1, 0, 0, 0, 0, 0 }));
		extensions.add(new sun.security.x509.KeyUsageExtension(new boolean[] { true, false, true, false, false, false, false, false }));
		Vector<sun.security.util.ObjectIdentifier> idenVector = new Vector<>();
		idenVector.add(new sun.security.util.ObjectIdentifier("1.3.6.1.5.5.7.3.1")); // ServerAuth
		idenVector.add(new sun.security.util.ObjectIdentifier("1.3.6.1.5.5.7.3.2")); // ClientAuth
		extensions.add(new sun.security.x509.ExtendedKeyUsageExtension(idenVector));
		
		// TODO 这里放进去怎么报错呢？
		byte[] sctBytes = new byte[] { 4, -126, 1, 104, 1, 102, 0, 117, 0, -24, 62, -48, -38, 62, -11, 6, 53, 50, -25, 87, 40, -68, -119, 107, -55, 3, -45, -53, -47, 17, 107, -20, -21, 105, -31, 119, 125, 109, 6, -67, 110, 0, 0, 1, 127, 41, -93, -77, -6, 0, 0, 4, 3, 0, 70, 48, 68, 2, 32, 32, -78, -118, 56, -30, 35, -83, -8, -119, 28, 32, -71, -4, 7, 14, 72, 9, -70, 66, -94, -67, 75, 114, -124, -22, 20, -55, 111, 89, 15, -78, -16, 2, 32, 51, 37, -32, 24, 99, -124, 32, 64, -50, -102, 89, 77, 48, -63, -84, -18, 31, -119, 11, -98, 24, -102, 105, -50, 108, 3, 16, 64, -14, -26, 40, 120, 0, 118, 0, 111, 83, 118, -84, 49, -16, 49, 25, -40, -103, 0, -92, 81, 21, -1, 119, 21, 28, 17, -39, 2, -63, 0, 41, 6, -115, -78, 8, -102, 55, -39, 19, 0, 0, 1, 127, 41, -93, -78, -26, 0, 0, 4, 3, 0, 71, 48, 69, 2, 33, 0, -43, 102, -82, -95, 96, -3, 76, -124, 10, -62, -61, -84, 118, 22, 115, -89, 59, -108, 56, 102, -38, -69, -77, -90, -36, -92, 57, 25, 32, 30, 70, -113, 2, 32, 87, -62, 1, -80, -31, -28, -47, -75, -64, 83, -33, -119, 19, -79, 66, 18, -112, 18, 75, -128, -22, 56, -39, -127, -44, -22, 38, -51, 57, -119, 35, 12, 0, 117, 0, 85, -127, -44, -62, 22, -112, 54, 1, 74, -22, 11, -101, 87, 60, 83, -16, -64, -28, 56, 120, 112, 37, 8, 23, 47, -93, -86, 29, 7, 19, -45, 12, 0, 0, 1, 127, 41, -93, -76, 27, 0, 0, 4, 3, 0, 70, 48, 68, 2, 32, 48, -5, 57, 1, -47, 90, -113, 112, 0, -79, 115, 1, -59, -96, 98, 43, -103, 80, -50, -40, 54, -36, -107, 20, -71, -90, -41, -94, 94, -31, 14, 86, 2, 32, 33, -13, -63, -26, 82, -24, -99, -119, -126, -8, 31, -93, -58, -12, -52, -62, 69, -38, 92, 48, -117, 13, 95, -18, 23, -18, 43, -118, -113, 55, -79, -55 };
		extensions.add(sun.security.x509.Extension.newExtension(ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 4, 1, 11129, 2, 4, 2 }), false, sctBytes));
		// extensions.add(new sun.security.x509.Extension(new sun.security.util.ObjectIdentifier("1.3.6.1.4.1.11129.2.4.2"), false, sctBytes));
		
		// TODO 授权密钥标识 AuthorityKey_Id
//		extensions.add(new sun.security.x509.AuthorityKeyIdentifierExtension());
		// TODO 使用者密钥标识
//		extensions.add(new sun.security.x509.SubjectKeyIdentifierExtension());
		
		// TODO 需要增加SCT 
		// 《证书透明记录作为强制检查项》https://www.sslchina.com/2018-april/
		// 《Java中的证书透明度验证》https://codeantenna.com/a/zQaIVBU5UD
		// 《SCT如何防止证书伪造》https://www.sohu.com/a/402489518_298038
		// 《证书透明度开发者网站》https://certificate.transparency.dev/
		// 《深度解读：证书中的SCT编码》https://www.sslchina.com/deep-dive-in-sct-encoding/
		// 《RFC6962》 https://www.rfc-editor.org/rfc/inline-errata/rfc6962.html
		
		
		// 这个序列号要动态生成
		Certificate serverCert = ___generateAppCert(issuer, "CN=" + domain, new BigInteger(System.currentTimeMillis() + ""),
				new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24),
				new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 32), keyPair.getPublic(), // 待签名的公钥
				caPrivateKey.getPrivateKey()// CA的私钥
				, extensions);
		
		return store(keyPair.getPrivate(), serverCert, caPrivateKey.getCertificate());
	}

	private static Certificate ___generateAppCert(String issuer, String subject, BigInteger serial, Date notBefore, Date notAfter, PublicKey publicKey, PrivateKey privKey, List<Extension> extensions) throws OperatorCreationException, CertificateException, IOException {
		
		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(new X500Name(RFC4519Style.INSTANCE, issuer), serial, notBefore, notAfter, new X500Name(subject), publicKey);
		// 这里不要使用SHA1算法，Chrome浏览器会提示「NET::ERR_CERT_WEAK_SIGNATURE_ALGORITHM」意为使用了过期的加密算法
		ContentSigner sigGen = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privKey);

		for (Extension ext : extensions) {
			if (ext.getId().equals("1.3.6.1.4.1.11129.2.4.2")) {
				builder.addExtension(new ASN1ObjectIdentifier(ext.getId()), ext.isCritical(), ext.getValue());
			} else {
				builder.addExtension(new ASN1ObjectIdentifier(ext.getId()), ext.isCritical(), ASN1Primitive.fromByteArray(ext.getValue()));
			}
		}
		
		X509CertificateHolder holder = builder.build(sigGen);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream is1 = new ByteArrayInputStream(holder.toASN1Structure().getEncoded());
		X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);
		is1.close();
		return theCert;
	}

	// 用KeyEntry形式存储一个私钥以及对应的证书，并把CA证书加入到它的信任证书列表里面。
	private static ByteArrayOutputStream store(PrivateKey appPrivateKey, Certificate appCert, Certificate caCert)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		
		KeyStore store = KeyStore.getInstance("PKCS12");
		store.load(null, null);
		// 我这里都是根据的域名进行读取，因此key的alias就没有用到了
		store.setKeyEntry("N/A", appPrivateKey, HttpsKeyStore.getKeyStorePassword(), new Certificate[] { appCert });
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		store.store(baos, HttpsKeyStore.getCertificatePassword());
		
		// 这里返回的是网站域名与之对应的公钥私钥+证书信息了，下一次使用是在org.hum.jmitm.ssl.HttpSslContextFactory.createSSLEngine
		return baos;
	}
	
	@Override
	public byte[] call() throws Exception {
		return _create(domain);
//		return _create2(domain);
	}
	
	@SuppressWarnings("unused")
	private byte[] _create2(String domain) throws Exception {
		ByteArrayOutputStream baos = GenCertAndKey.createCert(new X500Name("C = CN, ST = BeiJing, L = BeiJing, O = Apple Inc, OU = Dev, CN = jmitm"), domain);
		byte[] bytes = baos.toByteArray();
		baos.close();
		return bytes;
	}
}
