package fr.annway.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * [PERMISSION-TESTS] : chaque route protégée refuse l'appel sans session (401) et le mauvais
 * rôle (403) ; les routes publiques sont limitées en débit.
 */
class PermissionTest {

    private final WebFixtures web = new WebFixtures();

    private ApiResponse call(Route route, Optional<String> token) {
        var path = route.pattern().replaceAll("\\{[a-z]+}", "1");
        return route.method().equals("GET") ? web.get(path, token) : web.post(path, token, "");
    }

    @Test
    void everyProtectedRouteRefusesAnonymousCalls() {
        var protectedRoutes = web.api.router().routes().stream()
                .filter(route -> route.access() != Access.PUBLIC)
                .toList();
        assertFalse(protectedRoutes.isEmpty());
        for (var route : protectedRoutes) {
            assertEquals(HttpStatus.UNAUTHORIZED, call(route, Optional.empty()).status(), route.pattern());
        }
    }

    @Test
    void driverRoutesRefusePassengers() {
        var passenger = web.passenger("lea");
        web.api.router().routes().stream()
                .filter(route -> route.access() == Access.DRIVER)
                .forEach(route -> assertEquals(HttpStatus.FORBIDDEN, call(route, passenger).status(), route.pattern()));
    }

    @Test
    void passengerRoutesRefuseDrivers() {
        var driver = web.driver("hugo");
        web.api.router().routes().stream()
                .filter(route -> route.access() == Access.PASSENGER)
                .forEach(route -> assertEquals(HttpStatus.FORBIDDEN, call(route, driver).status(), route.pattern()));
    }

    @Test
    void unknownSessionIsAnonymous() {
        assertEquals(HttpStatus.UNAUTHORIZED, web.get("/api/me", Optional.of("inconnu")).status());
    }

    @Test
    void publicRoutesAreRateLimited() {
        for (var count = 0; count < RateLimiter.PUBLIC_LIMIT_PER_MINUTE; count++) {
            assertEquals(HttpStatus.OK, web.get("/api/health", Optional.empty()).status());
        }
        var refused = web.get("/api/health", Optional.empty());
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, refused.status());
    }

    @Test
    void unknownPathIs404AndWrongMethodIs405() {
        assertEquals(HttpStatus.NOT_FOUND, web.get("/api/nothing", Optional.empty()).status());
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, web.post("/api/health", Optional.empty(), "").status());
    }
}
