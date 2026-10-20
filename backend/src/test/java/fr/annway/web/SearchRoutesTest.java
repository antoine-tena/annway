package fr.annway.web;

import static fr.annway.web.WebFixtures.body;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class SearchRoutesTest {

    private static final String CRITERIA = "departure=Annecy&arrival=La+Clusaz&date=2026-12-16";

    private final WebFixtures web = new WebFixtures();
    private final Optional<String> passenger = web.passenger("lea");

    @Test
    void alertKeepsTheTripsPublishedAfterIt() {
        var created = web.post("/api/me/alerts", passenger, CRITERIA);
        assertEquals(HttpStatus.CREATED, created.status(), body(created));
        web.stationTrip(web.driver("hugo"));
        var alerts = body(web.get("/api/me/alerts", passenger));
        assertTrue(alerts.contains("\"isActive\":true"));
        assertTrue(alerts.contains("\"matchedTrips\":[{"));
    }

    @Test
    void inactiveAlertKeepsNothing() {
        web.post("/api/me/alerts", passenger, CRITERIA);
        var toggled = web.post("/api/me/alerts/0/toggle", passenger, "");
        assertTrue(body(toggled).contains("\"isActive\":false"));
        web.stationTrip(web.driver("hugo"));
        assertTrue(body(web.get("/api/me/alerts", passenger)).contains("\"matchedTrips\":[]"));
        assertTrue(body(web.post("/api/me/alerts/0/toggle", passenger, "")).contains("\"isActive\":true"));
    }

    @Test
    void unknownAlertIs404() {
        assertEquals(HttpStatus.NOT_FOUND, web.post("/api/me/alerts/3/toggle", passenger, "").status());
    }

    @Test
    void tripRequestIsSeenByItsPassengerAndTheDrivers() {
        var created = web.post("/api/me/trip-requests", passenger, CRITERIA + "&passengerCount=2&type=STATION");
        assertEquals(HttpStatus.CREATED, created.status(), body(created));
        assertTrue(body(created).contains("\"createdOn\":\"2026-12-14\""));
        assertTrue(body(web.get("/api/me/trip-requests", passenger)).contains("\"passengerCount\":2"));
        assertEquals("[]", body(web.get("/api/me/trip-requests", web.passenger("malo"))));
        assertTrue(body(web.get("/api/trip-requests", web.driver("hugo"))).contains("\"type\":\"STATION\""));
    }

    @Test
    void missingCriterionIsAnInvalidField() {
        var response = web.post("/api/me/trip-requests", passenger, "departure=Annecy&arrival=Semnoz");
        assertEquals(HttpStatus.BAD_REQUEST, response.status());
        assertTrue(body(response).contains("\"field\":\"date\""));
    }
}
