package com.ecc.util.crypto;

import org.apache.commons.codec.binary.Hex;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.ecc.constants.ApplicationConstants.HASH_ALGORITHM;

public final class HashUtil {

    public static String hash(String s) {
        String result = "";
        if (s == null || s.length() == 0) {
            return result;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
            messageDigest.update(s.getBytes());
            byte byteBuffer[] = messageDigest.digest();
            StringBuilder strHexString = new StringBuilder();
            for (byte aByteBuffer : byteBuffer) {
                String hex = Integer.toHexString(0xff & aByteBuffer);
                if (hex.length() == 1) {
                    strHexString.append('0');
                }
                strHexString.append(hex);
            }
            result = strHexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

        return result;
    }

    public static String hash(Path filePath) {
        try {
            MessageDigest myDigest = MessageDigest.getInstance(HASH_ALGORITHM);
            BufferedInputStream inputFile = new BufferedInputStream(new FileInputStream(filePath.toFile()));
            byte[] dataBytes = new byte[2048];
            int bytesFromFile;
            while ((bytesFromFile = inputFile.read(dataBytes, 0, 2048)) != -1) {
                myDigest.update(dataBytes, 0, bytesFromFile);
            }
            byte[] mdDigestArray = myDigest.digest();
            return Hex.encodeHexString(mdDigestArray);
        } catch (Exception e) {
            return "";
        }
    }
}
