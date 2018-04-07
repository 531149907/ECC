package com.ecc.util.converter;

public class OutputFormatter {
    public static final int MAX_LINE_LENGTH = 78;

    public static String format(int i, int padding) {
        return format(String.valueOf(i), padding);
    }

    public static String format(String s, int padding) {
        StringBuilder builder = new StringBuilder();
        if (s.length() >= MAX_LINE_LENGTH - padding) {
            String var0 = s;
            boolean firstLine = true;
            while (var0.length() >= MAX_LINE_LENGTH - padding + 1) {
                String temp = var0.substring(0, MAX_LINE_LENGTH - padding);
                var0 = var0.replace(temp, "");
                if (!firstLine) {
                    builder.append("        \t");
                } else {
                    firstLine = false;
                }
                builder.append(temp).append("\n");

            }
            builder.append("        \t").append(var0);
            return builder.toString();
        }
        return s;
    }
}
