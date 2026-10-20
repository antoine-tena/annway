package fr.annway.web;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Cookie de session ([COOKIES]) : préfixé {@code __Host-}, {@code HttpOnly}, {@code Secure},
 * {@code SameSite=Lax}. Les navigateurs l'acceptent aussi sur http://localhost, qu'ils tiennent
 * pour sûr.
 */
public final class SessionCookie {

    public static final String NAME = "__Host-annway_session";
    private static final String ATTRIBUTES = "; Path=/; HttpOnly; Secure; SameSite=Lax";

    private SessionCookie() {
    }

    public static String open(String token) {
        Objects.requireNonNull(token, "token");
        return NAME + "=" + token + "; Max-Age=" + SessionStore.SESSION_DURATION.toSeconds() + ATTRIBUTES;
    }

    public static String expired() {
        return NAME + "=; Max-Age=0" + ATTRIBUTES;
    }

    /** Jeton du cookie de session dans un en-tête {@code Cookie}, s'il y en a un. */
    public static Optional<String> read(String cookieHeader) {
        Objects.requireNonNull(cookieHeader, "cookieHeader");
        return Arrays.stream(cookieHeader.split(";"))
                .map(String::strip)
                .filter(pair -> pair.startsWith(NAME + "="))
                .map(pair -> pair.substring(NAME.length() + 1))
                .filter(token -> !token.isEmpty())
                .findFirst();
    }
}
