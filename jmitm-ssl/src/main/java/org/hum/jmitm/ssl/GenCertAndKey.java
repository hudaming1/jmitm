package org.hum.jmitm.ssl;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

public class GenCertAndKey {
	
	public static final String BC = BouncyCastleProvider.PROVIDER_NAME;
	private static final String caCertFilePath = "/Users/hudaming/Workspace/GitHub/jmitm/jmitm-ssl/src/test/java/org/hum/wiredog/ssl/test/bc2/ca/RootCA.crt";
	private static final String caKeyFilePath = "/Users/hudaming/Workspace/GitHub/jmitm/jmitm-ssl/src/test/java/org/hum/wiredog/ssl/test/bc2/ca/CA_Key.pem";
	private static final long expirationDay = 365;
	public static int BITS = 2048;
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static byte[] createCert(X500Name subject, String domain) throws Exception {
		// 生成私钥
		SecureRandom random = new SecureRandom();
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", BC);
		keyPairGenerator.initialize(BITS, random);
		KeyPair key = keyPairGenerator.generateKeyPair();

		// 加载证书
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		FileInputStream in = new FileInputStream(caCertFilePath);
		java.security.cert.Certificate c = cf.generateCertificate(in);

		X509Certificate x509Cert = (X509Certificate) c;

		Reader reader = new FileReader(caKeyFilePath);
		PemReader pemReader = new PemReader(reader);
		PemObject pemObject = pemReader.readPemObject();
		byte[] data = pemObject.getContent();
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data);
		PrivateKey issuerPrivateKey = kf.generatePrivate(keySpec);
		pemReader.close();

		PKCS10CertificationRequest csr = generateCSR(subject, key.getPublic(), key.getPrivate());
		Date notBefore = new Date();
		Date notAfter = new Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * expirationDay); // xx 天

		X509CertificateHolder issuer = new X509CertificateHolder(x509Cert.getEncoded());
		if (!verifyCSR(csr))
			throw new IllegalArgumentException("证书请求验证失败");

		BcX509ExtensionUtils extUtils = new BcX509ExtensionUtils();
		ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
		extensionsGenerator.addExtension(Extension.basicConstraints, false, new BasicConstraints(false)); // entity cert
		extensionsGenerator.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
		extensionsGenerator.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(new KeyPurposeId[] { KeyPurposeId.id_kp_serverAuth, KeyPurposeId.id_kp_clientAuth }));
		extensionsGenerator.addExtension(Extension.authorityKeyIdentifier, false, extUtils.createAuthorityKeyIdentifier(issuer)); // 授权密钥标识
		extensionsGenerator.addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(csr.getSubjectPublicKeyInfo())); // 使用者密钥标识

		GeneralName name1 = new GeneralName(GeneralName.iPAddress, "39.96.83.46");
		GeneralName name2 = new GeneralName(GeneralName.dNSName, "hudaming996.com");
		GeneralName[] name = { name1, name2 };
		GeneralNames subjectAltNames = new GeneralNames(name);
		extensionsGenerator.addExtension(Extension.subjectAlternativeName, false, subjectAltNames);
		V3TBSCertificateGenerator tbsGen = new V3TBSCertificateGenerator();
		tbsGen.setSerialNumber(new ASN1Integer(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE));
		tbsGen.setIssuer(issuer.getSubject());
		tbsGen.setStartDate(new Time(notBefore, Locale.CHINA));
		tbsGen.setEndDate(new Time(notAfter, Locale.CHINA));
		tbsGen.setSubject(csr.getSubject());
		tbsGen.setSubjectPublicKeyInfo(csr.getSubjectPublicKeyInfo());
		tbsGen.setExtensions(extensionsGenerator.generate());
		System.out.println(issuer.getSubjectPublicKeyInfo().getAlgorithm());

//		tbsGen.setSignature(new AlgorithmIdentifier(PKCSObjectIdentifiers.sha256WithRSAEncryption, DERNull.INSTANCE)); // 签名算法标识等于颁发者证书的密钥算法标识\
		tbsGen.setSignature(getSignAlgo(issuer.getSubjectPublicKeyInfo().getAlgorithm()));
		TBSCertificate tbs = tbsGen.generateTBSCertificate();
		Certificate certificate = assembleCert(tbs, issuer.getSubjectPublicKeyInfo(), issuerPrivateKey);
		
		// 写出证书
//		Writer certWriter = new FileWriter(targetCert);
		StringWriter sw = new StringWriter();
		PemWriter pemWriterCert = new PemWriter(sw);
		pemWriterCert.writeObject(new PemObject("CERTIFICATE", certificate.getEncoded()));
		pemWriterCert.flush();
		pemWriterCert.close();
		

		// 写出私钥
//		Writer w = new FileWriter(targetKey);
		PemWriter pemWriterKey = new PemWriter(sw);
		pemWriterKey.writeObject(new PemObject("RSA PRIVATE KEY", key.getPrivate().getEncoded()));
		pemWriterKey.flush();
		pemWriterKey.close();

		return sw.getBuffer().toString().getBytes();
	}

	private static PKCS10CertificationRequest generateCSR(X500Name subject, PublicKey publicKey,
			PrivateKey privateKey) {
		try {
			SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
			CertificationRequestInfo info = new CertificationRequestInfo(subject, subjectPublicKeyInfo, new DERSet());
			byte[] signature;
			AlgorithmIdentifier signAlgo = getSignAlgo(subjectPublicKeyInfo.getAlgorithm());
			if (signAlgo.getAlgorithm().equals(PKCSObjectIdentifiers.sha256WithRSAEncryption)) {
				signature = RSASign(info.getEncoded(ASN1Encoding.DER), privateKey);
			} else {
				throw new IllegalArgumentException("密钥算法不支持");
			}
			return new PKCS10CertificationRequest(
					new CertificationRequest(info, signAlgo, new DERBitString(signature)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("密钥结构错误");
		}
	}

	private static AlgorithmIdentifier getSignAlgo(AlgorithmIdentifier asymAlgo) { // 根据公钥算法标识返回对应签名算法标识
		if (asymAlgo.getAlgorithm().equals(PKCSObjectIdentifiers.rsaEncryption)) {
			return new AlgorithmIdentifier(PKCSObjectIdentifiers.sha256WithRSAEncryption, DERNull.INSTANCE);
		} else
			throw new IllegalArgumentException("密钥算法不支持");
	}

	public static Certificate assembleCert(TBSCertificate tbsCertificate,
			SubjectPublicKeyInfo issuerSubjectPublicKeyInfo, PrivateKey issuerPrivateKey) throws Exception {
		byte[] signature = null;
		if (issuerPrivateKey.getAlgorithm().equalsIgnoreCase("RSA")) {
			signature = RSASign(tbsCertificate.getEncoded(), issuerPrivateKey);
		} else {
			throw new IllegalArgumentException("不支持的密钥算法");
		}

		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(tbsCertificate);
		v.add(getSignAlgo(issuerSubjectPublicKeyInfo.getAlgorithm()));
		v.add(new DERBitString(signature));
		return Certificate.getInstance(new DERSequence(v));
	}

	private static boolean verifyCSR(PKCS10CertificationRequest csr) throws Exception {
		byte[] signature = csr.getSignature();
		if (csr.getSignatureAlgorithm().getAlgorithm().equals(PKCSObjectIdentifiers.sha256WithRSAEncryption)) {
			return RSAVerifySign(csr.toASN1Structure().getCertificationRequestInfo().getEncoded(ASN1Encoding.DER),
					signature, getPublicKey(csr.getSubjectPublicKeyInfo()));
		} else {
			throw new IllegalArgumentException("不支持的签名算法");
		}
	}

	private static byte[] RSASign(byte[] inData, PrivateKey privateKey) throws Exception {
		Signature signer = Signature.getInstance("SHA256WITHRSA", BC);
		signer.initSign(privateKey);
		signer.update(inData);
		return signer.sign();
	}

	private static PublicKey getPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws Exception {
		BouncyCastleProvider bouncyCastleProvider = ((BouncyCastleProvider) Security.getProvider(BC));
		bouncyCastleProvider.addKeyInfoConverter(PKCSObjectIdentifiers.rsaEncryption,
				new org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyFactorySpi());
		bouncyCastleProvider.addKeyInfoConverter(X9ObjectIdentifiers.id_ecPublicKey,
				new org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi.EC());
		return BouncyCastleProvider.getPublicKey(subjectPublicKeyInfo);
	}

	private static boolean RSAVerifySign(byte[] inData, byte[] signature, PublicKey publicKey) throws Exception {
		Signature signer = Signature.getInstance("SHA256WITHRSA", BC);
		signer.initVerify(publicKey);
		signer.update(inData);
		return signer.verify(signature);
	}
}