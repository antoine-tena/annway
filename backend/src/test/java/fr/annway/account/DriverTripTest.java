package fr.annway.account;

import static fr.annway.account.AccountFixtures.LATE_EVENING_CLOCK;
import static fr.annway.account.AccountFixtures.PRICE_PER_SEAT_CENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.annway.payment.PaymentService;
import fr.annway.search.SearchError;
import fr.annway.search.SearchException;
import fr.annway.search.TripCatalogue;
import fr.annway.trip.Reservation;
import fr.annway.trip.ReservationStatus;
import fr.annway.trip.StationTrip;
import fr.annway.trip.TripError;
import fr.annway.trip.TripException;
import java.util.List;
import org.junit.jupiter.api.Test;

class DriverTripTest {

    private static final PaymentService ACCEPTING = (payer, amountCents) -> true;

    private final Driver driver = AccountFixtures.driver(1);
    private final Driver otherDriver = new Driver(2, "Grace", "Hopper", "grace@example.org", "0622222222",
            List.of(AccountFixtures.vehicle("EF-456-GH")));
    private final StationTrip trip = AccountFixtures.stationTrip(1, driver);
    private final TripCatalogue catalogue = new TripCatalogue();

    @Test
    void publishedTripJoinsTheCatalogue() {
        driver.publishTrip(trip, catalogue);
        assertEquals(List.of(trip), catalogue.trips());
    }

    @Test
    void cannotPublishAnotherDriversTrip() {
        var error = assertThrows(SearchException.class, () -> otherDriver.publishTrip(trip, catalogue));
        assertEquals(SearchError.NOT_TRIP_DRIVER, error.error());
    }

    @Test
    void validatingPaidReservationTakesTheSeats() {
        var reservation = paidReservation(2);
        driver.validateReservation(reservation);
        assertEquals(ReservationStatus.VALIDATED, reservation.status());
        assertEquals(driver.vehicles().getFirst().seatCount() - 2, trip.availableSeats());
    }

    @Test
    void refusingKeepsTheSeats() {
        var reservation = paidReservation(2);
        driver.refuseReservation(reservation);
        assertEquals(ReservationStatus.REFUSED, reservation.status());
        assertEquals(driver.vehicles().getFirst().seatCount(), trip.availableSeats());
    }

    @Test
    void anotherDriverCannotAnswerTheReservation() {
        var reservation = paidReservation(1);
        var validateError = assertThrows(TripException.class, () -> otherDriver.validateReservation(reservation));
        var refuseError = assertThrows(TripException.class, () -> otherDriver.refuseReservation(reservation));
        assertEquals(TripError.NOT_TRIP_DRIVER, validateError.error());
        assertEquals(TripError.NOT_TRIP_DRIVER, refuseError.error());
        assertEquals(ReservationStatus.PENDING, reservation.status());
    }

    @Test
    void rejectsMissingValues() {
        assertThrows(NullPointerException.class, () -> driver.publishTrip(trip, null));
        assertThrows(NullPointerException.class, () -> driver.validateReservation(null));
        assertThrows(NullPointerException.class, () -> driver.refuseReservation(null));
    }

    private Reservation paidReservation(int seatCount) {
        var reservation = AccountFixtures.passenger(10).book(1, trip, seatCount, List.of(), LATE_EVENING_CLOCK);
        reservation.pay(PRICE_PER_SEAT_CENTS * seatCount, ACCEPTING, LATE_EVENING_CLOCK);
        return reservation;
    }
}
