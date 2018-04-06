package com.ecc.util.converter;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {
    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }
}
