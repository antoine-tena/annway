package fr.annway.trip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.annway.account.Driver;
import fr.annway.account.Vehicle;
import fr.annway.equipment.EquipmentFactory;
import fr.annway.equipment.EquipmentType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReservationTest {

    @Test
    void requestIsPendingAndTakesNoSeatYet() {
        var trip = TripFixtures.polytechTrip();
        var ski = EquipmentFactory.create(EquipmentType.SKI, "Skis");
        var reservation = Reservation.request(1, trip, TripFixtures.passenger(10), 2, List.of(ski),
                TripFixtures.BEFORE_DEPARTURE);
        assertEquals(ReservationStatus.PENDING, reservation.status());
        assertEquals(ski.bulk(), reservation.totalBulk());
        assertEquals(3, trip.availableSeats());
    }

    @Test
    void requestRejectsTooManySeats() {
        var error = assertThrows(TripException.class,
                () -> TripFixtures.reservation(TripFixtures.polytechTrip(), 4));
        assertEquals(TripError.NO_ROOM, error.error());
    }

    @Test
    void validationTakesSeatsAndFillsTheTrip() {
        var trip = TripFixtures.polytechTrip();
        var reservation = TripFixtures.reservation(trip, 3);
        reservation.validate(trip.driver());
        assertEquals(ReservationStatus.VALIDATED, reservation.status());
        assertEquals(0, trip.availableSeats());
        assertEquals(TripStatus.FULL, trip.status());
    }

    @Test
    void secondValidationFailsWhenSeatsAreGone() {
        var trip = TripFixtures.polytechTrip();
        var first = TripFixtures.reservation(trip, 2);
        var second = TripFixtures.reservation(trip, 2);
        first.validate(trip.driver());
        var error = assertThrows(TripException.class, () -> second.validate(trip.driver()));
        assertEquals(TripError.NO_ROOM, error.error());
    }

    @Test
    void onlyTheTripDriverDecides() {
        var trip = TripFixtures.polytechTrip();
        var reservation = TripFixtures.reservation(trip, 1);
        var other = new Driver(99, "Grace", "Hopper", "grace@example.org", "0622222222",
                List.of(new Vehicle("ZZ-999-ZZ", "Kangoo", 4, 4)));
        var error = assertThrows(TripException.class, () -> reservation.refuse(other));
        assertEquals(TripError.NOT_TRIP_DRIVER, error.error());
    }

    @Test
    void refusedReservationCannotBeValidated() {
        var trip = TripFixtures.polytechTrip();
        var reservation = TripFixtures.reservation(trip, 1);
        reservation.refuse(trip.driver());
        var error = assertThrows(TripException.class, () -> reservation.validate(trip.driver()));
        assertEquals(TripError.RESERVATION_NOT_PENDING, error.error());
    }

    @Test
    void cancellingAValidatedReservationReopensTheTrip() {
        var trip = TripFixtures.polytechTrip();
        var reservation = TripFixtures.reservation(trip, 3);
        reservation.validate(trip.driver());
        reservation.cancel(reservation.passenger());
        assertEquals(ReservationStatus.CANCELLED, reservation.status());
        assertEquals(3, trip.availableSeats());
        assertEquals(TripStatus.OPEN, trip.status());
    }

    @Test
    void onlyThePassengerCancels() {
        var reservation = TripFixtures.reservation(TripFixtures.polytechTrip(), 1);
        var error = assertThrows(TripException.class, () -> reservation.cancel(TripFixtures.passenger(11)));
        assertEquals(TripError.NOT_RESERVATION_PASSENGER, error.error());
    }

    @Test
    void cancelledReservationCannotBeCancelledAgain() {
        var reservation = TripFixtures.reservation(TripFixtures.polytechTrip(), 1);
        reservation.cancel(reservation.passenger());
        var error = assertThrows(TripException.class, () -> reservation.cancel(reservation.passenger()));
        assertEquals(TripError.RESERVATION_CLOSED, error.error());
    }
}
