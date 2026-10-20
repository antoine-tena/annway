package fr.annway.web;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Sessions ouvertes : un jeton aléatoire remis au navigateur dans un cookie, dont seule
 * l'empreinte SHA-256 est gardée ici. La recherche par empreinte ne compare jamais le jeton
 * lui-même ([CONSTANT-TIME]).
 */
public final class SessionStore {

    public static final Duration SESSION_DURATION = Duration.ofDays(7);
    private static final int TOKEN_BYTES = 32;

    private record Session(int userId, Instant expiresAt) {
    }

    private final SecureRandom random;
    private final Clock clock;
    private final Map<String, Session> sessionsByTokenHash = new HashMap<>();

    public SessionStore(SecureRandom random, Clock clock) {
        this.random = Objects.requireNonNull(random, "random");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /** Ouvre une session et renvoie son jeton, à ne transmettre qu'au navigateur. */
    public String open(int userId) {
        var bytes = new byte[TOKEN_BYTES];
        random.nextBytes(bytes);
        var token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sessionsByTokenHash.put(digest(token), new Session(userId, clock.instant().plus(SESSION_DURATION)));
        return token;
    }

    /** Compte de la session si le jeton est connu et pas expiré ; une session expirée est fermée. */
    public Optional<Integer> find(String token) {
        var key = digest(token);
        var session = sessionsByTokenHash.get(key);
        if (session == null) {
            return Optional.empty();
        }
        if (!clock.instant().isBefore(session.expiresAt())) {
            sessionsByTokenHash.remove(key);
            return Optional.empty();
        }
        return Optional.of(session.userId());
    }

    public void close(String token) {
        sessionsByTokenHash.remove(digest(token));
    }

    private static String digest(String token) {
        Objects.requireNonNull(token, "token");
        try {
            var hash = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException unavailable) {
            throw new IllegalStateException("SHA-256 indisponible dans ce JDK", unavailable);
        }
    }
}
