package org.hum.wiredog.ssl.test.bc;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class BC_Test {

	public static PKCS10CertificationRequest generateCSR(X500Name subject, PublicKey publicKey, PrivateKey privateKey) {
		try {
			SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
			CertificationRequestInfo info = new CertificationRequestInfo(subject, subjectPublicKeyInfo, new DERSet());
			byte[] signature;
			AlgorithmIdentifier signAlgo = getSignAlgo(subjectPublicKeyInfo.getAlgorithm());
			if (signAlgo.getAlgorithm().equals(GMObjectIdentifiers.sm2sign_with_sm3)) {
				signature = Signers.SM2Sign(info.getEncoded(ASN1Encoding.DER), privateKey);
			} else if (signAlgo.getAlgorithm().equals(X9ObjectIdentifiers.ecdsa_with_SHA256)) {
				signature = Signers.ECDSASign(info.getEncoded(ASN1Encoding.DER), privateKey);
			} else if (signAlgo.getAlgorithm().equals(PKCSObjectIdentifiers.sha256WithRSAEncryption)) {
				signature = Signers.RSASign(info.getEncoded(ASN1Encoding.DER), privateKey);
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

	// 生成实体证书
	public static Certificate certGen(PKCS10CertificationRequest csr, PrivateKey issuerPrivateKey, byte[] issuerCert,
			Date notBefore, Date notAfter) throws Exception {
		X509CertificateHolder issuer = new X509CertificateHolder(issuerCert);
		if (!verifyCSR(csr))
			throw new IllegalArgumentException("证书请求验证失败");
		X500Name subject = csr.getSubject();
		BcX509ExtensionUtils extUtils = new BcX509ExtensionUtils();
		ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
		extensionsGenerator.addExtension(Extension.basicConstraints, true, new BasicConstraints(false)); // entity cert
		extensionsGenerator.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
		extensionsGenerator.addExtension(Extension.authorityKeyIdentifier, false,
				extUtils.createAuthorityKeyIdentifier(issuer)); // 授权密钥标识
		extensionsGenerator.addExtension(Extension.subjectKeyIdentifier, false,
				extUtils.createSubjectKeyIdentifier(csr.getSubjectPublicKeyInfo())); // 使用者密钥标识
		V3TBSCertificateGenerator tbsGen = new V3TBSCertificateGenerator();
		tbsGen.setSerialNumber(new ASN1Integer(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE));
		tbsGen.setIssuer(issuer.getSubject());
		tbsGen.setStartDate(new Time(notBefore, Locale.CHINA));
		tbsGen.setEndDate(new Time(notAfter, Locale.CHINA));
		tbsGen.setSubject(subject);
		tbsGen.setSubjectPublicKeyInfo(csr.getSubjectPublicKeyInfo());
		tbsGen.setExtensions(extensionsGenerator.generate());
		tbsGen.setSignature(issuer.getSubjectPublicKeyInfo().getAlgorithm()); // 签名算法标识等于颁发者证书的密钥算法标识
		TBSCertificate tbs = tbsGen.generateTBSCertificate();
		return assembleCert(tbs, issuer.getSubjectPublicKeyInfo(), issuerPrivateKey);
	}

	private static Certificate assembleCert(TBSCertificate tbsCertificate,
			SubjectPublicKeyInfo issuerSubjectPublicKeyInfo, PrivateKey issuerPrivateKey) throws Exception {
		byte[] signature = null;
		if (issuerPrivateKey.getAlgorithm().equalsIgnoreCase("ECDSA"))
			if (issuerSubjectPublicKeyInfo.getAlgorithm().getParameters().equals(GMObjectIdentifiers.sm2p256v1)) {
				signature = Signers.SM2Sign(tbsCertificate.getEncoded(), issuerPrivateKey);
			} else if (issuerSubjectPublicKeyInfo.getAlgorithm().getParameters()
					.equals(SECObjectIdentifiers.secp256k1)) {
				signature = Signers.ECDSASign(tbsCertificate.getEncoded(), issuerPrivateKey);
			} else
				throw new IllegalArgumentException("不支持的曲线");
		else if (issuerPrivateKey.getAlgorithm().equalsIgnoreCase("RSA")) {
			signature = Signers.RSASign(tbsCertificate.getEncoded(), issuerPrivateKey);
		} else
			throw new IllegalArgumentException("不支持的密钥算法");
		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(tbsCertificate);
		v.add(getSignAlgo(issuerSubjectPublicKeyInfo.getAlgorithm()));
		v.add(new DERBitString(signature));
		return Certificate.getInstance(new DERSequence(v));
	}

	private static boolean verifyCSR(PKCS10CertificationRequest csr) throws Exception {
		byte[] signature = csr.getSignature();
		if (csr.getSignatureAlgorithm().getAlgorithm().equals(GMObjectIdentifiers.sm2sign_with_sm3)) {
			return Signers.SM2VerifySign(
					csr.toASN1Structure().getCertificationRequestInfo().getEncoded(ASN1Encoding.DER), signature,
					KeyUtils.getPublicKey(csr.getSubjectPublicKeyInfo()));
		} else if (csr.getSignatureAlgorithm().getAlgorithm().equals(X9ObjectIdentifiers.ecdsa_with_SHA256)) {
			return Signers.ECDSAVerifySign(
					csr.toASN1Structure().getCertificationRequestInfo().getEncoded(ASN1Encoding.DER), signature,
					KeyUtils.getPublicKey(csr.getSubjectPublicKeyInfo()));
		} else if (csr.getSignatureAlgorithm().getAlgorithm().equals(PKCSObjectIdentifiers.sha256WithRSAEncryption)) {
			return Signers.RSAVerifySign(
					csr.toASN1Structure().getCertificationRequestInfo().getEncoded(ASN1Encoding.DER), signature,
					KeyUtils.getPublicKey(csr.getSubjectPublicKeyInfo()));
		} else {
			throw new IllegalArgumentException("不支持的签名算法");
		}
	}

	private static AlgorithmIdentifier getSignAlgo(AlgorithmIdentifier asymAlgo) { // 根据公钥算法标识返回对应签名算法标识
		if (asymAlgo.getAlgorithm().equals(X9ObjectIdentifiers.id_ecPublicKey)
				&& asymAlgo.getParameters().equals(GMObjectIdentifiers.sm2p256v1)) {
			return new AlgorithmIdentifier(GMObjectIdentifiers.sm2sign_with_sm3, DERNull.INSTANCE);
		} else if (asymAlgo.getAlgorithm().equals(X9ObjectIdentifiers.id_ecPublicKey)
				&& asymAlgo.getParameters().equals(SECObjectIdentifiers.secp256k1)) {
			return new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA256);
		} else if (asymAlgo.getAlgorithm().equals(PKCSObjectIdentifiers.rsaEncryption)) {
			return new AlgorithmIdentifier(PKCSObjectIdentifiers.sha256WithRSAEncryption, DERNull.INSTANCE);
		} else
			throw new IllegalArgumentException("密钥算法不支持");
	}

	public static void main(String[] args) throws Exception {

	}
}
