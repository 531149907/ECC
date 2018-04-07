package com.ecc.util.converter;

import java.util.Base64;

public final class Base64Util {
    public static String encode(byte[] src) {
        return Base64.getEncoder().encodeToString(src);
    }

    public static byte[] decode(String src) {
        return Base64.getDecoder().decode(src);
    }
}
