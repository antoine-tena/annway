package fr.annway.web;

import static fr.annway.web.WebFixtures.body;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TripRoutesTest {

    private final WebFixtures web = new WebFixtures();
    private final Optional<String> driver = web.driver("hugo");

    @Test
    void publishedTripIsInTheCatalogueWithItsPlaces() {
        web.stationTrip(driver);
        var catalogue = body(web.get("/api/trips", Optional.empty()));
        assertTrue(catalogue.contains("\"arrival\":\"La Clusaz\""));
        assertTrue(catalogue.contains("\"departures\":[\"Annecy\"]"));
        // 7 h 30 à Paris en décembre : 6 h 30 UTC.
        assertTrue(catalogue.contains("\"departureAt\":\"2026-12-16T06:30:00Z\""));
        assertTrue(catalogue.contains("\"pricePerSeatCents\":800"));
    }

    @Test
    void searchUsesTheCriteriaAndTheFilters() {
        web.stationTrip(driver);
        var criteria = "/api/trips?departure=annecy&arrival=la+clusaz&date=2026-12-16&passengerCount=2";
        assertFalse(hasNoTrip(criteria));
        assertTrue(hasNoTrip(criteria + "&maxPriceCents=500"));
        assertTrue(hasNoTrip(criteria + "&earliest=08:00"));
        assertFalse(hasNoTrip(criteria + "&earliest=07:00&latest=08:00"));
        assertTrue(hasNoTrip(criteria.replace("2026-12-16", "2026-12-17")));
    }

    private boolean hasNoTrip(String path) {
        return body(web.get(path, Optional.empty())).startsWith("{\"trips\":[]");
    }

    @Test
    void invertedTimeWindowIsADomainError() {
        var response = web.get("/api/trips?earliest=10:00&latest=08:00", Optional.empty());
        assertEquals(HttpStatus.UNPROCESSABLE, response.status());
        assertTrue(body(response).contains("INVERTED_TIME_WINDOW"));
    }

    @Test
    void polytechTripCarriesItsBus() {
        var response = web.post("/api/trips", driver, "type=POLYTECH&departure=Polytech+Annecy"
                + "&arrival=Gare+routi%C3%A8re&departureAt=2026-12-16T07:00&busDepartureAt=2026-12-16T07:30"
                + "&servedStation=La+Clusaz&registration=aa-111-aa");
        assertEquals(HttpStatus.CREATED, response.status(), body(response));
        assertTrue(body(response).contains("\"servedStation\":\"La Clusaz\""));
        assertTrue(body(response).contains("\"isPaymentRequired\":false"));
    }

    @Test
    void unknownVehicleIsAnInvalidField() {
        var response = web.post("/api/trips", driver, "type=STATION&departure=Annecy&arrival=Semnoz"
                + "&departureAt=2026-12-16T07:30&registration=ZZ-999-ZZ&pricePerSeatCents=500");
        assertEquals(HttpStatus.BAD_REQUEST, response.status());
    }

    @Test
    void driverAddsAStopAndListsHisTrips() {
        var tripId = web.stationTrip(driver);
        var stop = web.post("/api/trips/" + tripId + "/stops", driver, "place=Th%C3%B4nes&passingAt=2026-12-16T07:45");
        assertEquals(HttpStatus.CREATED, stop.status(), body(stop));
        assertTrue(body(web.get("/api/trips/" + tripId, Optional.empty())).contains("\"place\":\"Thônes\""));
        assertTrue(body(web.get("/api/me/trips", driver)).contains("La Clusaz"));
    }

    @Test
    void driverCancelsHisTripButNotAnotherOne() {
        var tripId = web.stationTrip(driver);
        var other = web.driver("camille");
        assertEquals(HttpStatus.FORBIDDEN, web.post("/api/trips/" + tripId + "/cancel", other, "").status());
        var cancelled = web.post("/api/trips/" + tripId + "/cancel", driver, "");
        assertTrue(body(cancelled).contains("\"status\":\"CANCELLED\""));
        assertTrue(hasNoTrip("/api/trips"));
    }

    @Test
    void tripFinishesOnlyAfterDeparture() {
        var tripId = web.stationTrip(driver);
        var early = web.post("/api/trips/" + tripId + "/finish", driver, "");
        assertEquals(HttpStatus.UNPROCESSABLE, early.status());
        assertTrue(body(early).contains("TRIP_NOT_DEPARTED"));
    }

    @Test
    void finishedTripLeavesTheUpcomingList() {
        var later = new WebFixtures(Clock.fixed(WebFixtures.NOW.plusSeconds(3 * 86_400), ZoneOffset.UTC));
        var hugo = later.driver("hugo");
        var tripId = later.stationTrip(hugo);
        var finished = later.post("/api/trips/" + tripId + "/finish", hugo, "");
        assertTrue(body(finished).contains("\"status\":\"FINISHED\""));
    }

    @Test
    void unknownTripIs404() {
        assertEquals(HttpStatus.NOT_FOUND, web.get("/api/trips/99", Optional.empty()).status());
        assertEquals(HttpStatus.NOT_FOUND, web.get("/api/trips/abc", Optional.empty()).status());
    }
}
