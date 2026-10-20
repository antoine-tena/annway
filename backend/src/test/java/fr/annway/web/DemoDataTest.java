package fr.annway.web;

import static fr.annway.web.WebFixtures.body;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class DemoDataTest {

    private final WebFixtures web = new WebFixtures();

    private void load(Optional<String> password) {
        new DemoData(web.api.store(), web.api.credentials(), WebFixtures.CLOCK).load(password);
    }

    @Test
    void demoTripsAreInTheCatalogue() {
        load(Optional.empty());
        var catalogue = body(web.get("/api/trips", Optional.empty()));
        assertTrue(catalogue.contains("\"arrival\":\"La Clusaz\""));
        assertTrue(catalogue.contains("\"arrival\":\"Semnoz\""));
        assertTrue(catalogue.contains("\"type\":\"POLYTECH\""));
    }

    @Test
    void demoAccountsLogInOnlyWithTheDemoPassword() {
        load(Optional.empty());
        var refused = web.post("/api/auth/login", Optional.empty(), "email=camille@example.org&password=x");
        assertEquals(HttpStatus.UNAUTHORIZED, refused.status());
        var other = new WebFixtures();
        new DemoData(other.api.store(), other.api.credentials(), WebFixtures.CLOCK).load(Optional.of(WebFixtures.PASSWORD));
        var accepted = other.post("/api/auth/login", Optional.empty(),
                "email=camille@example.org&password=" + WebFixtures.PASSWORD);
        assertEquals(HttpStatus.OK, accepted.status());
        assertTrue(body(accepted).contains("\"isVerified\":true"));
    }
}
