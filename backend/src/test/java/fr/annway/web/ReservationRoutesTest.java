package fr.annway.web;

import static fr.annway.web.WebFixtures.body;
import static fr.annway.web.WebFixtures.idOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class ReservationRoutesTest {

    private final WebFixtures web = new WebFixtures();
    private final Optional<String> driver = web.driver("hugo");
    private final Optional<String> passenger = web.passenger("lea");
    private final int tripId = web.stationTrip(driver);

    private int book(String form) {
        var response = web.post("/api/trips/" + tripId + "/reservations", passenger, form);
        assertEquals(HttpStatus.CREATED, response.status(), body(response));
        return idOf(body(response));
    }

    @Test
    void passengerBooksPaysAndDriverValidates() {
        var reservationId = book("seatCount=2&equipment=SKI:Skis+170&equipment=SLED:Luge");
        var paid = web.post("/api/reservations/" + reservationId + "/pay", passenger, "amountCents=1600");
        assertEquals(HttpStatus.OK, paid.status(), body(paid));
        assertTrue(body(paid).contains("\"type\":\"FIXED_PRICE\""));
        var validated = web.post("/api/reservations/" + reservationId + "/validate", driver, "");
        assertTrue(body(validated).contains("\"status\":\"VALIDATED\""));
        var trip = body(web.get("/api/trips/" + tripId, Optional.empty()));
        assertTrue(trip.contains("\"availableSeats\":1"));
        assertTrue(trip.contains("\"remainingEquipmentCapacity\":1"));
    }

    @Test
    void unpaidStationTripCannotBeValidated() {
        var reservationId = book("seatCount=1");
        var response = web.post("/api/reservations/" + reservationId + "/validate", driver, "");
        assertEquals(HttpStatus.UNPROCESSABLE, response.status());
        assertTrue(body(response).contains("PAYMENT_REQUIRED"));
    }

    @Test
    void wrongAmountIsRefused() {
        var reservationId = book("seatCount=1");
        var response = web.post("/api/reservations/" + reservationId + "/pay", passenger, "amountCents=1");
        assertTrue(body(response).contains("WRONG_AMOUNT"));
    }

    @Test
    void bothSeeTheReservationOthersDoNot() {
        book("seatCount=1");
        assertTrue(body(web.get("/api/me/reservations", passenger)).contains("\"seatCount\":1"));
        assertTrue(body(web.get("/api/me/reservations", driver)).contains("\"seatCount\":1"));
        assertEquals("[]", body(web.get("/api/me/reservations", web.passenger("malo"))));
    }

    @Test
    void onlyThePassengerPaysOrCancelsAndOnlyTheDriverDecides() {
        var reservationId = book("seatCount=1");
        var malo = web.passenger("malo");
        assertEquals(HttpStatus.FORBIDDEN, web.post("/api/reservations/" + reservationId + "/pay", malo,
                "amountCents=800").status());
        assertEquals(HttpStatus.FORBIDDEN, web.post("/api/reservations/" + reservationId + "/cancel", malo, "").status());
        var camille = web.driver("camille");
        assertEquals(HttpStatus.FORBIDDEN, web.post("/api/reservations/" + reservationId + "/refuse", camille, "").status());
    }

    @Test
    void driverRefusesAndPassengerCancels() {
        var refusedId = book("seatCount=1");
        var refused = web.post("/api/reservations/" + refusedId + "/refuse", driver, "");
        assertTrue(body(refused).contains("\"status\":\"REFUSED\""));
        var cancelledId = book("seatCount=1");
        var cancelled = web.post("/api/reservations/" + cancelledId + "/cancel", passenger, "");
        assertTrue(body(cancelled).contains("\"status\":\"CANCELLED\""));
    }

    @Test
    void malformedEquipmentIsAnInvalidField() {
        var noSeparator = web.post("/api/trips/" + tripId + "/reservations", passenger, "seatCount=1&equipment=SKI");
        assertEquals(HttpStatus.BAD_REQUEST, noSeparator.status());
        var unknownType = web.post("/api/trips/" + tripId + "/reservations", passenger, "seatCount=1&equipment=LUGE:x");
        assertEquals(HttpStatus.BAD_REQUEST, unknownType.status());
    }

    @Test
    void tooManySeatsIsNoRoom() {
        var response = web.post("/api/trips/" + tripId + "/reservations", passenger, "seatCount=9");
        assertTrue(body(response).contains("NO_ROOM"));
        assertFalse(body(web.get("/api/me/reservations", passenger)).contains("\"id\""));
    }

    @Test
    void unknownReservationIs404() {
        assertEquals(HttpStatus.NOT_FOUND, web.post("/api/reservations/42/cancel", passenger, "").status());
    }
}
