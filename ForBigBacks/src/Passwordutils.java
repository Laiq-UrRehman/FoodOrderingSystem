// FIX (Critical): Utility class for hashing and verifying passwords using
//   SHA-256 with a per-user salt. Passwords are never stored in plain text.
//
//   Usage:
//     String[] hashed = PasswordUtils.hashPassword("mypassword");
//     // hashed[0] = salt, hashed[1] = hash — store both in Customer
//
//     boolean ok = PasswordUtils.verify("mypassword", storedSalt, storedHash);

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {

    private static final int SALT_BYTES = 16;

    /** Returns {salt (Base64), hash (Base64)} for a plain-text password. */
    public static String[] hashPassword(String plainText) {
        byte[] saltBytes = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(saltBytes);
        String salt = Base64.getEncoder().encodeToString(saltBytes);
        String hash = hash(plainText, salt);
        return new String[]{salt, hash};
    }

    /** Returns true if plainText matches the stored salt + hash. */
    public static boolean verify(String plainText, String salt, String storedHash) {
        return hash(plainText, salt).equals(storedHash);
    }

    private static String hash(String plainText, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] digest = md.digest(plainText.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }
}