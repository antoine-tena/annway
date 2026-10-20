package fr.annway.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Mots de passe, sessions, limite de débit, clé client et cookie. */
class SecurityToolsTest {

    private final MovableClock clock = new MovableClock(WebFixtures.NOW);

    @Test
    void passwordHashMatchesOnlyItsPassword() {
        var hasher = new PasswordHasher(new SecureRandom(), 1);
        var stored = hasher.hash(WebFixtures.PASSWORD);
        assertTrue(hasher.matches(WebFixtures.PASSWORD, stored));
        assertFalse(hasher.matches("autre-mot-de-passe", stored));
        assertNotEquals(stored, hasher.hash(WebFixtures.PASSWORD));
        assertEquals(stored, new PasswordHash(stored.salt(), stored.hash()));
        assertEquals(stored.hashCode(), new PasswordHash(stored.salt(), stored.hash()).hashCode());
        assertFalse(stored.toString().contains(String.valueOf(stored.hash()[0])));
        assertThrows(IllegalArgumentException.class, () -> new PasswordHasher(new SecureRandom(), 0));
    }

    @Test
    void sessionExpiresAfterItsDuration() {
        var sessions = new SessionStore(new SecureRandom(), clock);
        var token = sessions.open(7);
        assertEquals(Optional.of(7), sessions.find(token));
        clock.advance(SessionStore.SESSION_DURATION);
        assertEquals(Optional.empty(), sessions.find(token));
    }

    @Test
    void closedSessionIsForgotten() {
        var sessions = new SessionStore(new SecureRandom(), clock);
        var token = sessions.open(7);
        sessions.close(token);
        assertEquals(Optional.empty(), sessions.find(token));
    }

    @Test
    void rateLimitResetsWithTheWindow() {
        var limiter = new RateLimiter(clock, 2);
        limiter.acquire(WebFixtures.CLIENT);
        limiter.acquire(WebFixtures.CLIENT);
        var refused = assertThrows(WebException.class, () -> limiter.acquire(WebFixtures.CLIENT));
        assertEquals(WebError.TOO_MANY_REQUESTS, refused.error());
        assertDoesNotThrow(() -> limiter.acquire("198.51.100.0"));
        clock.advance(RateLimiter.WINDOW);
        assertDoesNotThrow(() -> limiter.acquire(WebFixtures.CLIENT));
        assertThrows(IllegalArgumentException.class, () -> new RateLimiter(clock, 0));
    }

    @Test
    void clientKeyDropsTheEndOfTheAddress() throws Exception {
        assertEquals("192.0.2.0", ClientKey.of(InetAddress.getByName("192.0.2.17")));
        assertEquals("2001:db8:1:2:0:0:0:0", ClientKey.of(InetAddress.getByName("2001:db8:1:2:3:4:5:6")));
    }

    @Test
    void sessionCookieIsReadAmongOthers() {
        assertEquals(Optional.of("abc"), SessionCookie.read("theme=dark; " + SessionCookie.NAME + "=abc"));
        assertEquals(Optional.empty(), SessionCookie.read(SessionCookie.NAME + "="));
        assertEquals(Optional.empty(), SessionCookie.read("theme=dark"));
        assertTrue(SessionCookie.open("abc").contains("Max-Age=" + Duration.ofDays(7).toSeconds()));
    }
}
