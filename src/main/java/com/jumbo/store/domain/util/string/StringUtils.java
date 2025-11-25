package com.jumbo.store.domain.util.string;

public class StringUtils {

    private StringUtils() {}

    public static String cleanupErrorMessage(String value) {
        if (value == null) {
            return null;
        }
        String cleaned = value;
        int firstQuote = cleaned.indexOf('"');
        if (firstQuote != -1) {
            cleaned = cleaned.substring(firstQuote + 1);
        }
        if (cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }
        return cleaned;
    }

    public static boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }
}
