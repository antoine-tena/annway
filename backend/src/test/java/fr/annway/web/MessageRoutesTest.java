package fr.annway.web;

import static fr.annway.web.WebFixtures.body;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class MessageRoutesTest {

    private final WebFixtures web = new WebFixtures();

    @Test
    void membersTalkUnderATrip() {
        var driver = web.driver("hugo");
        var tripId = web.stationTrip(driver);
        var passenger = web.passenger("lea");
        var sent = web.post("/api/trips/" + tripId + "/messages", passenger, "content=Une+place+pour+mes+skis+%3F");
        assertEquals(HttpStatus.CREATED, sent.status(), body(sent));
        var thread = body(web.get("/api/trips/" + tripId + "/messages", driver));
        assertTrue(thread.contains("\"content\":\"Une place pour mes skis ?\""));
        assertTrue(thread.contains("\"firstName\":\"lea\""));
    }

    @Test
    void emptyMessageIsAnInvalidField() {
        var driver = web.driver("hugo");
        var tripId = web.stationTrip(driver);
        assertEquals(HttpStatus.BAD_REQUEST, web.post("/api/trips/" + tripId + "/messages", driver, "content=").status());
        assertEquals(HttpStatus.NOT_FOUND, web.get("/api/trips/77/messages", driver).status());
        assertEquals(HttpStatus.OK, web.get("/api/equipment-types", Optional.empty()).status());
    }
}
