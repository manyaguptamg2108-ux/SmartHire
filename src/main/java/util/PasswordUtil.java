package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for SHA-256 password hashing and verification with whitespace resilience.
 */
public class PasswordUtil {

    private PasswordUtil() {}

    /**
     * Hashes a raw text password using SHA-256. Automatically trims leading/trailing spaces.
     *
     * @param password Raw password string
     * @return 64-character hex string representing the SHA-256 digest
     */
    public static String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        String cleanPassword = password.trim();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(cleanPassword.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies if a raw password matches a stored SHA-256 hash.
     *
     * @param rawPassword Input password to verify
     * @param storedHash Stored hex hash string
     * @return true if hash matches
     */
    public static boolean verifyPassword(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null || rawPassword.trim().isEmpty() || storedHash.trim().isEmpty()) {
            return false;
        }
        String computedHash = hashPassword(rawPassword.trim());
        return computedHash.equalsIgnoreCase(storedHash.trim());
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
