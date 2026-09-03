package com.fitsync.util;

import java.util.regex.Pattern;

/**
 * Small collection of input-validation helpers shared by the controllers.
 * All methods are null-safe and operate on trimmed input.
 */
public final class ValidationUtil {

    /** Minimum accepted password length. */
    public static final int MIN_PASSWORD_LENGTH = 6;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }

    public static boolean isPositiveNumber(String text) {
        if (!isNotEmpty(text)) {
            return false;
        }
        try {
            return Double.parseDouble(text.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }
}
