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
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class CreateCaTest {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	// openssl pkcs12 -in atlas-ca.p12 -nokeys -out cert.crt
	private static final char[] PASS = "".toCharArray();

	public static void main(String[] args) {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair keyPair = kpg.generateKeyPair();
			KeyStore store = KeyStore.getInstance("PKCS12");
			store.load(null, null);
			String issuer = "C=CN,ST=HeBei,L=Qinhuangdao,E=512657752@qq.com";
			String subject = issuer;
			// issuer 与 subject相同的证书就是CA证书
			Certificate cert = generateV3(issuer, subject, BigInteger.ZERO,
					new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24),
					new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 32), keyPair.getPublic(), // 待签名的公钥
					keyPair.getPrivate(), null);
			store.setKeyEntry("atlas", keyPair.getPrivate(), PASS, new Certificate[] { cert });
			cert.verify(keyPair.getPublic());
			File file = new File("/tmp/jmitm_ca.p12");
			if (file.exists() || file.createNewFile()) {
				store.store(new FileOutputStream(file), PASS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Certificate generateV3(String issuer, String subject, BigInteger serial, Date notBefore, Date notAfter, PublicKey publicKey, PrivateKey privKey, List<Extension> extensions) throws OperatorCreationException, CertificateException, IOException {

		X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(new X500Name(issuer), serial, notBefore, notAfter, new X500Name(subject), publicKey);
		ContentSigner sigGen = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privKey);
//		for (Extension ext : extensions) {
//			builder.addExtension(new ASN1ObjectIdentifier(ext.getOid()), ext.isCritical(), ASN1Primitive.fromByteArray(ext.getValue()));
//		}
		
		X509CertificateHolder holder = builder.build(sigGen);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream is1 = new ByteArrayInputStream(holder.toASN1Structure().getEncoded());
		X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);
		is1.close();
		return theCert;
	}
	
	public class Extension {
		private String oid;
		private boolean critical;
		private byte[] value;

		public String getOid() {
			return oid;
		}

		public byte[] getValue() {
			return value;
		}

		public boolean isCritical() {
			return critical;
		}
	}
}
