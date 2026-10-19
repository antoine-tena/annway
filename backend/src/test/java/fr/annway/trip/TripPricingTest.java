package fr.annway.trip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TripPricingTest {

    @Test
    void stationTripChargesThePricePerSeat() {
        var trip = TripFixtures.stationTrip();
        assertEquals(TripType.STATION, trip.type());
        assertTrue(trip.isPaymentRequired());
        assertEquals(3 * TripFixtures.PRICE_PER_SEAT_CENTS, trip.amountDueCents(3));
    }

    @Test
    void stationTripRejectsFreePrice() {
        var driver = TripFixtures.driver();
        var error = assertThrows(TripException.class,
                () -> new StationTrip(TripFixtures.route(), driver, driver.vehicles().getFirst(), 0));
        assertEquals(TripError.INVALID_PRICE, error.error());
    }

    @Test
    void polytechTripOwesNothing() {
        var trip = TripFixtures.polytechTrip();
        assertEquals(TripType.POLYTECH, trip.type());
        assertFalse(trip.isPaymentRequired());
        assertEquals(0, trip.amountDueCents(2));
        assertEquals("Chamrousse", trip.servedStation());
    }

    @Test
    void polytechBusCannotLeaveBeforeTheTrip() {
        var driver = TripFixtures.driver();
        var route = new TripRoute(2, "Grenoble", "Polytech", TripFixtures.LATE_EVENING);
        var error = assertThrows(TripException.class, () -> new PolytechTrip(route, driver,
                driver.vehicles().getFirst(), TripFixtures.LATE_EVENING.minusSeconds(60), "Chamrousse"));
        assertEquals(TripError.BUS_BEFORE_TRIP_DEPARTURE, error.error());
    }

    @Test
    void amountDueRejectsZeroSeat() {
        var error = assertThrows(TripException.class, () -> TripFixtures.polytechTrip().amountDueCents(0));
        assertEquals(TripError.INVALID_SEAT_COUNT, error.error());
    }
}
