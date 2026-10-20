package fr.annway.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class HttpEntryTest {

    private final WebFixtures web = new WebFixtures();
    private final HttpEntry entry = new HttpEntry(web.api.router());

    private FakeExchange send(String method, String uri, String contentType, String body) throws IOException {
        var exchange = new FakeExchange(method, uri, body);
        if (!contentType.isEmpty()) {
            exchange.getRequestHeaders().set("Content-Type", contentType);
        }
        entry.handle(exchange);
        return exchange;
    }

    /** [SECURITY-HEADERS] : chaque réponse porte les en-têtes de sécurité. */
    @Test
    void everyResponseCarriesTheSecurityHeaders() throws IOException {
        var exchange = send("GET", "/api/health", "", "");
        assertEquals(HttpStatus.OK, exchange.getResponseCode());
        assertEquals("{\"status\":\"ok\"}", exchange.responseText());
        HttpEntry.SECURITY_HEADERS.forEach((name, value) ->
                assertEquals(value, exchange.getResponseHeaders().getFirst(name), name));
        assertTrue(exchange.getResponseHeaders().getFirst("Content-Type").startsWith("application/json"));
    }

    @Test
    void formBodyAndSessionCookieGoThrough() throws IOException {
        var form = "application/x-www-form-urlencoded; charset=UTF-8";
        var registered = send("POST", "/api/auth/register", form, "role=PASSENGER&firstName=Lea&lastName=T"
                + "&email=lea@example.org&phone=06&password=" + WebFixtures.PASSWORD);
        assertEquals(HttpStatus.CREATED, registered.getResponseCode(), registered.responseText());
        var cookie = registered.getResponseHeaders().getFirst("Set-Cookie").split(";")[0];
        var me = new FakeExchange("GET", "/api/trips?date=2026-12-16", "");
        me.getRequestHeaders().set("Cookie", cookie);
        entry.handle(me);
        assertEquals(HttpStatus.OK, me.getResponseCode());
        var logout = new FakeExchange("POST", "/api/auth/logout", "");
        logout.getRequestHeaders().set("Cookie", cookie);
        entry.handle(logout);
        assertEquals(HttpStatus.NO_CONTENT, logout.getResponseCode());
    }

    @Test
    void otherContentTypesAreIgnoredAndHugeBodiesRefused() throws IOException {
        var json = send("POST", "/api/auth/login", "application/json", "{\"email\":\"x\"}");
        assertEquals(HttpStatus.BAD_REQUEST, json.getResponseCode());
        var huge = send("POST", "/api/auth/login", "application/x-www-form-urlencoded",
                "a=" + "x".repeat(HttpEntry.MAX_BODY_BYTES));
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, huge.getResponseCode());
    }

    @Test
    void unexpectedErrorIs500WithoutDetails() {
        var failing = new Router(List.of(Route.get("/api/boom", Access.PUBLIC, call -> {
            throw new IllegalStateException("détail interne");
        })), new SessionStore(new SecureRandom(), Clock.systemUTC()), new AnnwayStore(),
                new RateLimiter(WebFixtures.CLOCK, 1));
        var response = failing.handle(new ApiRequest("GET", "/api/boom", FormData.empty(), FormData.empty(),
                Optional.empty(), WebFixtures.CLIENT));
        assertEquals(HttpStatus.INTERNAL_ERROR, response.status());
        assertTrue(response.body().orElseThrow().contains("INTERNAL"));
    }
}
