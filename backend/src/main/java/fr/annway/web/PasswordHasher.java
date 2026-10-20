package fr.annway.web;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Objects;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hache les mots de passe par PBKDF2-HMAC-SHA256, avec un sel aléatoire par compte ; la
 * vérification compare en temps constant ([CONSTANT-TIME]).
 */
public final class PasswordHasher {

    /** Recommandation OWASP pour PBKDF2-HMAC-SHA256. */
    public static final int DEFAULT_ITERATIONS = 600_000;
    static final int MIN_PASSWORD_LENGTH = 10;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;

    private final SecureRandom random;
    private final int iterations;

    public PasswordHasher(SecureRandom random, int iterations) {
        this.random = Objects.requireNonNull(random, "random");
        if (iterations < 1) {
            throw new IllegalArgumentException("iterations");
        }
        this.iterations = iterations;
    }

    /** Hache un nouveau mot de passe ; trop court, il est refusé. */
    public PasswordHash hash(String password) {
        Objects.requireNonNull(password, "password");
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new WebException(WebError.WEAK_PASSWORD, "password");
        }
        var salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        return new PasswordHash(salt, derive(password, salt));
    }

    public boolean matches(String password, PasswordHash stored) {
        Objects.requireNonNull(password, "password");
        Objects.requireNonNull(stored, "stored");
        return MessageDigest.isEqual(derive(password, stored.salt()), stored.hash());
    }

    private byte[] derive(String password, byte[] salt) {
        var spec = new PBEKeySpec(password.toCharArray(), salt, iterations, HASH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException unavailable) {
            throw new IllegalStateException("PBKDF2 indisponible dans ce JDK", unavailable);
        } finally {
            spec.clearPassword();
        }
    }
}
