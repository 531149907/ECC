package com.ecc.util.crypto;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public final class AesUtil {
    private static final String ALG = "AES";
    private static final String ENC = "UTF-8";
    private static final String SEC_NORMALIZE_ALG = "MD5";

    public static String encrypt(String secret, String data) {
        try {
            MessageDigest dig = null;
            dig = MessageDigest.getInstance(SEC_NORMALIZE_ALG);
            byte[] key = dig.digest(secret.getBytes(ENC));
            SecretKeySpec secKey = new SecretKeySpec(key, ALG);

            Cipher aesCipher = Cipher.getInstance(ALG);
            byte[] byteText = data.getBytes(ENC);

            aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
            byte[] byteCipherText = aesCipher.doFinal(byteText);

            Base64 base64 = new Base64();
            return new String(base64.encode(byteCipherText), ENC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decrypt(String secret, String data) {
        try {
            MessageDigest dig = MessageDigest.getInstance(SEC_NORMALIZE_ALG);
            byte[] key = dig.digest(secret.getBytes(ENC));
            SecretKeySpec secKey = new SecretKeySpec(key, ALG);

            Cipher aesCipher = Cipher.getInstance(ALG);
            aesCipher.init(Cipher.DECRYPT_MODE, secKey);
            Base64 base64 = new Base64();
            byte[] bytes = base64.decode(data.getBytes());
            byte[] bytePlainText = aesCipher.doFinal(bytes);

            return new String(bytePlainText, ENC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
