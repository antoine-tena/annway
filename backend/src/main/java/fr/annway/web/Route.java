package fr.annway.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Une route de l'API : méthode, motif de chemin ({@code /api/trips/{id}}), authentification
 * déclarée ([PERMISSIONS]) et traitement.
 */
public record Route(String method, String pattern, Access access, ApiHandler handler) {

    public Route {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(pattern, "pattern");
        Objects.requireNonNull(access, "access");
        Objects.requireNonNull(handler, "handler");
    }

    public static Route get(String pattern, Access access, ApiHandler handler) {
        return new Route("GET", pattern, access, handler);
    }

    public static Route post(String pattern, Access access, ApiHandler handler) {
        return new Route("POST", pattern, access, handler);
    }

    /** Paramètres du chemin si le chemin suit le motif, quelle que soit la méthode. */
    Optional<Map<String, String>> matchPath(String path) {
        var expected = pattern.split("/");
        var actual = path.split("/");
        if (expected.length != actual.length) {
            return Optional.empty();
        }
        var params = new HashMap<String, String>();
        for (var index = 0; index < expected.length; index++) {
            var segment = expected[index];
            if (segment.startsWith("{") && segment.endsWith("}")) {
                params.put(segment.substring(1, segment.length() - 1), actual[index]);
            } else if (!segment.equals(actual[index])) {
                return Optional.empty();
            }
        }
        return Optional.of(params);
    }
}
