package com.ecc.util.crypto;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

public final class AesUtil {
    private static final String ALG = "AES";
    private static final String ENC = "UTF-8";
    private static final String SEC_NORMALIZE_ALG = "MD5";

    public static String encrypt(String secret, String data) {
        try {
            MessageDigest dig = MessageDigest.getInstance(SEC_NORMALIZE_ALG);
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

    public static void encrypt(String secret, Path inputFilePath, Path outputFilePath) throws Exception {
        try {
            if (!Files.exists(outputFilePath)) {
                Files.createFile(outputFilePath);
            }

            InputStream inputStream = new FileInputStream(inputFilePath.toFile());
            OutputStream outputStream = new FileOutputStream(outputFilePath.toFile());

            MessageDigest dig = MessageDigest.getInstance(SEC_NORMALIZE_ALG);
            byte[] key = dig.digest(secret.getBytes(ENC));
            SecretKeySpec secKey = new SecretKeySpec(key, ALG);
            Cipher aesCipher = Cipher.getInstance(ALG);
            aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
            CipherInputStream cipherInputStream = new CipherInputStream(inputStream, aesCipher);

            byte[] cache = new byte[1024];
            int read;
            while ((read = cipherInputStream.read(cache)) != -1) {
                outputStream.write(cache, 0, read);
                outputStream.flush();
            }

            outputStream.close();
            cipherInputStream.close();
            inputStream.close();
        } catch (Exception e) {
            throw new Exception("Encrypt file failed!");
        }
    }

    public static void decrypt(String secret, Path inputFilePath, Path outputFilePath) throws Exception {
        try {
            if (!Files.exists(outputFilePath)) {
                Files.createFile(outputFilePath);
            }

            if(Files.exists(inputFilePath)){
                InputStream inputStream = new FileInputStream(inputFilePath.toFile());
                OutputStream outputStream = new FileOutputStream(outputFilePath.toFile());

                MessageDigest dig = MessageDigest.getInstance(SEC_NORMALIZE_ALG);
                byte[] key = dig.digest(secret.getBytes(ENC));
                SecretKeySpec secKey = new SecretKeySpec(key, ALG);
                Cipher aesCipher = Cipher.getInstance(ALG);
                aesCipher.init(Cipher.DECRYPT_MODE, secKey);
                CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, aesCipher);

                byte[] cache = new byte[1024];
                int read;
                while ((read = inputStream.read(cache)) != -1) {
                    cipherOutputStream.write(cache, 0, read);
                    cipherOutputStream.flush();
                }

                cipherOutputStream.close();
                outputStream.close();
                inputStream.close();
            }
        } catch (Exception e) {
            throw new Exception("Failed to decrypt file!");
        }
    }
}
