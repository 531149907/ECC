package com.ecc.util.crypto;

import com.ecc.domain.peer.Peer;
import com.ecc.domain.security.KeyStorage;
import com.ecc.exceptions.KeyException;
import com.ecc.util.converter.Base64Util;
import com.ecc.util.converter.BytesUtil;
import sun.misc.BASE64Decoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.ecc.constants.PeerConstants.*;

public final class RsaUtil {
    private static final Path KEY_BASE_PATH = Paths.get(PATH_KEY);

    public static void generateKeyPair(String email) {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ENCRYPT_ALGORITHM_ASYMMETRIC);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (keyPairGenerator != null) {
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            if (!Files.exists(KEY_BASE_PATH)) {
                try {
                    Files.createDirectories(KEY_BASE_PATH);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            email = HashUtil.hash(email);

            Path publicKeyPath = Paths.get(PATH_KEY + email + SUFFIX_PUBLIC_KEY);
            Path privateKeyPath = Paths.get(PATH_KEY + email + SUFFIX_PRIVATE_KEY);

            if (Files.exists(publicKeyPath) || Files.exists(privateKeyPath)) {
                return;
            }

            try {
                byte[] publicKeyBytes = BytesUtil.toBytes(publicKey);
                Files.createFile(publicKeyPath);
                Files.write(publicKeyPath, publicKeyBytes);

                byte[] privateKeyBytes = BytesUtil.toBytes(privateKey);
                Files.createFile(privateKeyPath);
                Files.write(privateKeyPath, privateKeyBytes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static KeyStorage loadKeyPair(String email) {
        email = HashUtil.hash(email);
        Path publicKeyPath = Paths.get(PATH_KEY + email + SUFFIX_PUBLIC_KEY);
        Path privateKeyPath = Paths.get(PATH_KEY + email + SUFFIX_PRIVATE_KEY);
        if (Files.exists(publicKeyPath) && Files.exists(privateKeyPath)) {
            try {
                byte[] publicBytes = Files.readAllBytes(publicKeyPath);
                byte[] privateBytes = Files.readAllBytes(privateKeyPath);
                PublicKey publicKey = (PublicKey) BytesUtil.toObject(publicBytes);
                PrivateKey privateKey = (PrivateKey) BytesUtil.toObject(privateBytes);
                return new KeyStorage(publicKey, privateKey);
            } catch (IOException e) {
                throw new KeyException("load key files error!");
            }
        }
        throw new KeyException("key files not exists!");
    }

    private static byte[] encrypt(byte[] data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM_ASYMMETRIC);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String data, PublicKey publicKey) {
        return Base64Util.encode(encrypt(data.getBytes(), publicKey));
    }

    public static String encrypt(String data, String publicKey) {
        return Base64Util.encode(encrypt(data.getBytes(), getPublicKeyFromString(publicKey)));
    }

    private static byte[] decrypt(byte[] data, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM_ASYMMETRIC);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String data, PrivateKey privateKey) {
        byte[] bytes = Base64Util.decode(data);
        return new String(decrypt(bytes, privateKey));
    }

    public static String decrypt(String data, String privateKey) {
        byte[] bytes = Base64Util.decode(data);
        return new String(decrypt(bytes, getPrivateKeyFromString(privateKey)));
    }

    public static String sign(String rawMessage, String privateKey) {
        String hashedMessage = HashUtil.hash(rawMessage);
        return Base64Util.encode(sign(getPrivateKeyFromString(privateKey), hashedMessage.getBytes()));
    }

    public static String sign(String rawMessage, PrivateKey privateKey) {
        String hashedMessage = HashUtil.hash(rawMessage);
        return Base64Util.encode(sign(privateKey, hashedMessage.getBytes()));
    }

    private static byte[] sign(PrivateKey privateKey, byte[] data) {
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sign(String rawMessage) {
        KeyStorage keyStorage = RsaUtil.loadKeyPair(Peer.getPeer().getEmail());
        PrivateKey privateKey = keyStorage.getPrivateKey();
        String hashedMessage = HashUtil.hash(rawMessage);
        return Base64Util.encode(sign(privateKey, hashedMessage.getBytes()));
    }

    private static boolean verify(PublicKey publicKey, byte[] rawData, byte[] signedData) {
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(rawData);
            return signature.verify(signedData);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean verify(PublicKey publicKey, String rawMessage, String signedMessage) {
        String hashedRawMessage = HashUtil.hash(rawMessage);
        return verify(publicKey, hashedRawMessage.getBytes(), Base64Util.decode(signedMessage));
    }

    public static boolean verify(String publicKey, String rawMessage, String signedMessage) {
        String hashedRawMessage = HashUtil.hash(rawMessage);
        return verify(getPublicKeyFromString(publicKey), hashedRawMessage.getBytes(), Base64Util.decode(signedMessage));
    }

    public static String getKeyInString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static String getKeyInString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static PublicKey getPublicKeyFromString(String publicKey) {
        try {
            byte[] keyBytes;
            keyBytes = (new BASE64Decoder()).decodeBuffer(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPT_ALGORITHM_ASYMMETRIC);
            PublicKey key = keyFactory.generatePublic(keySpec);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey getPrivateKeyFromString(String privateKey) {
        try {
            byte[] keyBytes;
            keyBytes = (new BASE64Decoder()).decodeBuffer(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPT_ALGORITHM_ASYMMETRIC);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
