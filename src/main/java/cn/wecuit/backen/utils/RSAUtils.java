package cn.wecuit.backen.utils;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtils {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    public static void init(String privateKeyStr, String publicKeyStr) throws Exception {
        if(null != privateKeyStr)
            privateKey = getPrivateKey(privateKeyStr);

        if(null != publicKeyStr)
            publicKey = getPublicKey(publicKeyStr);
    }

    public static String encryptRSAByPubKey(String str) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        rsa.init(Cipher.ENCRYPT_MODE, publicKey);   //getPrivateKey(privateKey):将privateKey转成pkcs1的密文形式

        byte[] data = str.getBytes();
        int inputLen = data.length;
        int offset = 0;
        byte[] cache;
        int i = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //分段加密 参考：https://www.cnblogs.com/duanjt/p/11459600.html
        while(inputLen -offset > 0){
            if(inputLen - offset > MAX_ENCRYPT_BLOCK)
                cache = rsa.doFinal(data, offset, MAX_ENCRYPT_BLOCK);
            else
                cache = rsa.doFinal(data, offset, inputLen - offset);
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        String result = new String(Base64.getEncoder().encode(out.toByteArray()));
        out.close();
        return result;
    }
    public static String decryptRSAByPriKey(String str) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        rsa.init(Cipher.DECRYPT_MODE, privateKey);   //getPrivateKey(privateKey):将privateKey转成pkcs1的密文形式

        byte[] data = Base64.getDecoder().decode(str);
        int inputLen = data.length;
        int offset = 0;
        byte[] cache;
        int i = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //分段加密
        while(inputLen -offset > 0){
            if(inputLen - offset > MAX_DECRYPT_BLOCK)
                cache = rsa.doFinal(data, offset, MAX_DECRYPT_BLOCK);
            else
                cache = rsa.doFinal(data, offset, inputLen - offset);
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        String result = out.toString("UTF-8");
        out.close();
        return result;
    }

    /**
     * 参考： https://blog.csdn.net/weixin_43303530/article/details/111479748
     * @param privateKey
     * @return
     * @throws Exception
     */
    private static PrivateKey getPrivateKey(String privateKey) throws Exception {
        String privKeyPEMNew = privateKey.replaceAll("\\n", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace(" ", "");
        byte[] e = Base64.getDecoder().decode(privKeyPEMNew);   //base64无法解析换行符,-等特殊符号
        PrivateKeyInfo pki = PrivateKeyInfo.getInstance(e);
        RSAPrivateKey pkcs1Key = RSAPrivateKey.getInstance(pki.parsePrivateKey());
        //RSAPrivateKeyStructure pkcs1Key = RSAPrivateKeyStructure.getInstance(pki.parsePrivateKey());
        //byte[] pkcs1Bytes = pkcs1Key.getEncoded();
        // 读取 PKCS#1的私钥
        //RSAPrivateKeyStructure asn1PrivateKey = new RSAPrivateKeyStructure((ASN1Sequence) ASN1Sequence.fromByteArray(pkcs1Bytes));
        //RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(asn1PrivateKey.getModulus(),asn1PrivateKey.getPrivateExponent());   此处注释的为过时方法，同样可以完成所需的效果
        RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(pkcs1Key.getModulus(),pkcs1Key.getPrivateExponent());
        // 实例化KeyFactory对象，并指定 RSA 算法
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // 获得 PrivateKey 对象
        return keyFactory.generatePrivate(rsaPrivateKeySpec);
    }

    /**
     *
     * 参考：https://blog.csdn.net/chaiqunxing51/article/details/52116433?locationNum=3&fps=1
     * @param publicKey
     * @return
     * @throws Exception
     */
    private static PublicKey getPublicKey(String publicKey) throws Exception {
        String pubKeyPEMNew = publicKey.replaceAll("\\n", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace(" ", "");
        byte[] e = Base64.getDecoder().decode(pubKeyPEMNew);   //base64无法解析换行符,-等特殊符号
        // RSAPublicKey pki = RSAPublicKey.getInstance(e);
        // RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(pki.getModulus(), pki.getPublicExponent());
        // // 实例化KeyFactory对象，并指定 RSA 算法
        // KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(e);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(pubX509);

        // 获得 PrivateKey 对象
        // return keyFactory.generatePublic(rsaPublicKeySpec);
    }

    public static byte[] genMD5(byte[] data) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("md5").digest(data);
    }
}