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
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.hum.jmitm.ssl.common.GetCert;
import org.hum.jmitm.ssl.common.HttpsKeyStore;

import lombok.extern.slf4j.Slf4j;
import sun.security.util.ObjectIdentifier;

@Slf4j
@SuppressWarnings("restriction")
public class CA_Creator implements Callable<byte[]> {
	
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
	
	public CA_Creator(String domain) {
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
		
		X509Certificate serverRealCert = GetCert.getCert(domain);
		
		// 生成一组非对称加密键值对，后续作为动态网站证书的公钥和私钥
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair keyPair = kpg.generateKeyPair();

		sun.security.x509.X500Name caX500Name = (sun.security.x509.X500Name) caCert.getSubjectDN();
		// 这里取了rfc2253，下面用的是rfc4519，两者格式能兼容？
		String issuer = caX500Name.getRFC2253Name();
		
		//
		List<Extension> extensions = new ArrayList<>();
		
		Set<String> caExtOids = serverRealCert.getCriticalExtensionOIDs();
		
		
		System.out.println("getNonCriticalExtensionOIDs:" + serverRealCert.getNonCriticalExtensionOIDs().size() + "=" + serverRealCert.getNonCriticalExtensionOIDs());
		System.out.println("getCriticalExtensionOIDs:" + serverRealCert.getCriticalExtensionOIDs().size() + "=" + serverRealCert.getCriticalExtensionOIDs());
		System.out.println("getExtendedKeyUsage:" + serverRealCert.getExtendedKeyUsage().size() + "=" + serverRealCert.getExtendedKeyUsage());
		
		if (serverRealCert.getNonCriticalExtensionOIDs() != null) {
			for (String extId : serverRealCert.getNonCriticalExtensionOIDs()) {
				// ExtendedKeyUsages在下面循环中添加，这里就不再重复添加
				if ("2.5.29.37".equals(extId) || "2.5.29.35".equals(extId)) {
					continue;
				}
				String[] split = extId.split("\\.");
				int[] oid = new int[split.length];
				for (int i = 0 ;i < split.length ;i ++) {
					oid[i] = Integer.parseInt(split[i]);
				}
				try {
					extensions.add(new sun.security.x509.Extension(ObjectIdentifier.newInternal(oid), caExtOids.contains(extId), serverRealCert.getExtensionValue(extId)));
				} catch (Exception ce) {
					System.err.println(extId  + " is error");
					ce.printStackTrace();
				}
			}
		}

		if (serverRealCert.getCriticalExtensionOIDs() != null) {
			for (String extId : serverRealCert.getCriticalExtensionOIDs()) {
				String[] split = extId.split("\\.");
				int[] oid = new int[split.length];
				for (int i = 0 ;i < split.length ;i ++) {
					oid[i] = Integer.parseInt(split[i]);
				}
				try {
					extensions.add(new sun.security.x509.Extension(ObjectIdentifier.newInternal(oid), caExtOids.contains(extId), serverRealCert.getExtensionValue(extId)));
				} catch (Exception ce) {
					System.err.println(extId  + " is error");
					ce.printStackTrace();
				}
			}
		}
		
		Vector<sun.security.util.ObjectIdentifier> idenVector = new Vector<>();
		if (serverRealCert.getExtendedKeyUsage() != null) {
			for (String extId : serverRealCert.getExtendedKeyUsage()) {
				idenVector.add(new sun.security.util.ObjectIdentifier(extId));
			}
		}
		extensions.add(new sun.security.x509.ExtendedKeyUsageExtension(idenVector));
		
		// 将自身的标识和颁发者的关联上
		extensions.add(new sun.security.x509.Extension(ObjectIdentifier.newInternal(new int[] { 2, 5, 29, 35 }), false, caCert.getExtensionValue("2.5.29.35")));
		
		// 这个序列号要动态生成
		Certificate serverCert = ___generateAppCert(issuer, serverRealCert.getSubjectDN().getName(), new BigInteger(System.nanoTime() + ""),
				serverRealCert.getNotBefore(),
				serverRealCert.getNotAfter(), keyPair.getPublic(), // 待签名的公钥
				caPrivateKey.getPrivateKey()// CA的私钥
				, extensions);
		
		return store(keyPair.getPrivate(), serverCert, caCert);
	}

	private static Certificate ___generateAppCert(String issuer, String subject, BigInteger serial, Date notBefore, Date notAfter, PublicKey publicKey, PrivateKey privKey, List<Extension> extensions) throws OperatorCreationException, CertificateException, IOException {
		
		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(new X500Name(RFC4519Style.INSTANCE, issuer), serial, notBefore, notAfter, new X500Name(RFC4519Style.INSTANCE, subject), publicKey);
		// 这里不要使用SHA1算法，Chrome浏览器会提示「NET::ERR_CERT_WEAK_SIGNATURE_ALGORITHM」意为使用了过期的加密算法
		ContentSigner sigGen = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privKey);

		for (Extension ext : extensions) {
			try {
				if (ext.getId().equals("1.3.6.1.4.1.11129.2.4.2")) {
					builder.addExtension(new ASN1ObjectIdentifier(ext.getId()), ext.isCritical(), ext.getValue());
 				} else if ("2.5.29.31".equals(ext.getId()) || "2.5.29.32".equals(ext.getId()) || "1.3.6.1.5.5.7.1.1".equals(ext.getId())) {
 					continue;
 				} else {
 					builder.addExtension(new ASN1ObjectIdentifier(ext.getId()), ext.isCritical(), ext.getValue());
 				}
				System.out.println(ext.getId() + " added to cert.");
			} catch (Exception ce) {
				log.error("extension[" + ext.getId() + "] is error", ce);
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
