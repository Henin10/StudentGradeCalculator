package util;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {
    private static final int ITERATIONS = 120_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hash(String password) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] derivedKey = deriveKey(password, salt);
        return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(derivedKey);
    }

    public static boolean verify(String password, String encodedHash) {
        if (password == null || encodedHash == null) {
            return false;
        }

        String[] parts = encodedHash.split(":", 3);
        if (parts.length != 3) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expectedKey = Base64.getDecoder().decode(parts[2]);
            byte[] actualKey = deriveKey(password, salt, iterations);
            if (expectedKey.length != actualKey.length) {
                return false;
            }

            int difference = 0;
            for (int i = 0; i < expectedKey.length; i++) {
                difference |= expectedKey[i] ^ actualKey[i];
            }
            return difference == 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] deriveKey(String password, byte[] salt) {
        return deriveKey(password, salt, ITERATIONS);
    }

    private static byte[] deriveKey(String password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing is unavailable.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
