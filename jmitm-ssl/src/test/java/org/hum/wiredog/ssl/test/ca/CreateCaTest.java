package org.hum.wiredog.ssl.test.ca;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
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

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.hum.jmitm.ssl.CA_Creator;

public class CreateCaTest {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	@SuppressWarnings("restriction")
	public static void main(String[] args) {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair keyPair = kpg.generateKeyPair();
			KeyStore store = KeyStore.getInstance("PKCS12");
			store.load(null, null);
			String issuer = "C=CN,ST=HeBei,L=Qinhuangdao,E=512657752@qq.com,CN=Jmitm";
			String subject = issuer;
			
			List<Extension> extensions = new ArrayList<>();
			extensions.add(new sun.security.x509.BasicConstraintsExtension(true, 1));
			// 秘钥用途：Digital Signature, Certificate Signing, CRL Signing (标志位含义参考：KeyUsageExtension.set方法)
			extensions.add(new sun.security.x509.KeyUsageExtension(new boolean[] { true, false, false, false, false, true, true, false }));
			// oid=2.5.29.35:主题标识，签证时将该值签到子证书上
			extensions.add(new sun.security.x509.AuthorityKeyIdentifierExtension(new sun.security.x509.KeyIdentifier(keyPair.getPublic()), null, null));
			
			// issuer 与 subject相同的证书就是CA证书
			Certificate cert = generateV3(issuer, subject, new BigInteger(System.nanoTime() + ""),
					new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24),
					new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 32), keyPair.getPublic(), // 待签名的公钥
					keyPair.getPrivate(), extensions);
			store.setKeyEntry(CA_Creator.CA_ALIAS, keyPair.getPrivate(), CA_Creator.CA_PASS, new Certificate[] { cert });
			cert.verify(keyPair.getPublic());
			File file = new File("/tmp/jmitm_ca.p12");
			if (file.exists() || file.createNewFile()) {
				store.store(new FileOutputStream(file), CA_Creator.CA_PASS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// openssl pkcs12 -in /tmp/jmitm_ca.p12 -nokeys -out /tmp/cert.crt

		// openssl x509 -in /tmp/cert.crt -out /tmp/my_key_store.cer -outform der

	}
	
	public static Certificate generateV3(String issuer, String subject, BigInteger serial, Date notBefore, Date notAfter, PublicKey publicKey, PrivateKey privKey, List<Extension> extensions) throws OperatorCreationException, CertificateException, IOException {

		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(new X500Name(issuer), serial, notBefore, notAfter, new X500Name(subject), publicKey);
		ContentSigner sigGen = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privKey);
		for (Extension ext : extensions) {
 			builder.addExtension(new ASN1ObjectIdentifier(ext.getId()), ext.isCritical(), ext.getValue());
		}
		X509CertificateHolder holder = builder.build(sigGen);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream is1 = new ByteArrayInputStream(holder.toASN1Structure().getEncoded());
		X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);
		is1.close();
		return theCert;
	}
}
