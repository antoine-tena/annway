package fr.annway.account;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/** Code à usage unique envoyé par e-mail ou par SMS pour vérifier un moyen de contact. */
public final class VerificationCode {

    static final Duration VALIDITY = Duration.ofMinutes(15);
    private static final int CODE_BOUND = 1_000_000;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final String code;
    private final Instant expiresAt;

    private VerificationCode(String code, Instant expiresAt) {
        this.code = code;
        this.expiresAt = expiresAt;
    }

    static VerificationCode issue(Clock clock) {
        var code = "%06d".formatted(RANDOM.nextInt(CODE_BOUND));
        return new VerificationCode(code, clock.instant().plus(VALIDITY));
    }

    public String code() {
        return code;
    }

    boolean matches(String candidate, Clock clock) {
        Objects.requireNonNull(candidate, "candidate");
        return clock.instant().isBefore(expiresAt) && code.equals(candidate.strip());
    }
}
