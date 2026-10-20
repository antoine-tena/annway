package fr.annway.web;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Limite de débit des routes publiques ([PERMISSIONS]) : au plus {@code limit} requêtes par
 * client sur une fenêtre fixe. Le client est désigné par son adresse tronquée.
 */
public final class RateLimiter {

    public static final int PUBLIC_LIMIT_PER_MINUTE = 60;
    public static final Duration WINDOW = Duration.ofMinutes(1);

    private final Clock clock;
    private final int limit;
    private final Map<String, Integer> countsByClient = new HashMap<>();
    private Instant windowStart;

    public RateLimiter(Clock clock, int limit) {
        this.clock = Objects.requireNonNull(clock, "clock");
        if (limit < 1) {
            throw new IllegalArgumentException("limit");
        }
        this.limit = limit;
        this.windowStart = clock.instant();
    }

    /** Compte la requête ; au-delà de la limite, lève {@link WebError#TOO_MANY_REQUESTS}. */
    public void acquire(String clientKey) {
        Objects.requireNonNull(clientKey, "clientKey");
        var now = clock.instant();
        if (!now.isBefore(windowStart.plus(WINDOW))) {
            countsByClient.clear();
            windowStart = now;
        }
        var count = countsByClient.merge(clientKey, 1, Integer::sum);
        if (count > limit) {
            throw new WebException(WebError.TOO_MANY_REQUESTS);
        }
    }
}
