package com.jumbo.store.domain.util.string;

public class StringUtils {

    private StringUtils() {}

    /**
     * Removes surrounding quotes from error messages.
     * Handles cases where error messages are wrapped in quotes like "error message".
     *
     * @param value the error message to clean
     * @return cleaned message without surrounding quotes, or null if input is null
     */
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
}
