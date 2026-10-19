package fr.annway.trip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ReservationPaymentTest {

    private static final long TWO_SEATS_CENTS = 2 * TripFixtures.PRICE_PER_SEAT_CENTS;

    @Test
    void stationTripNeedsPaymentBeforeValidation() {
        var trip = TripFixtures.stationTrip();
        var reservation = TripFixtures.reservation(trip, 2);
        var error = assertThrows(TripException.class, () -> reservation.validate(trip.driver()));
        assertEquals(TripError.PAYMENT_REQUIRED, error.error());
    }

    @Test
    void paidStationReservationCanBeValidated() {
        var trip = TripFixtures.stationTrip();
        var reservation = TripFixtures.reservation(trip, 2);
        var payment = reservation.pay(TWO_SEATS_CENTS, TripFixtures.ACCEPTING, TripFixtures.BEFORE_DEPARTURE);
        assertEquals(PaymentType.FIXED_PRICE, payment.type());
        reservation.validate(trip.driver());
        assertEquals(ReservationStatus.VALIDATED, reservation.status());
    }

    @Test
    void stationTripRefusesAnotherAmount() {
        var reservation = TripFixtures.reservation(TripFixtures.stationTrip(), 2);
        var error = assertThrows(TripException.class,
                () -> reservation.pay(TWO_SEATS_CENTS - 1, TripFixtures.ACCEPTING, TripFixtures.BEFORE_DEPARTURE));
        assertEquals(TripError.WRONG_AMOUNT, error.error());
    }

    @Test
    void declinedPaymentIsNotRecorded() {
        var reservation = TripFixtures.reservation(TripFixtures.stationTrip(), 2);
        var error = assertThrows(TripException.class,
                () -> reservation.pay(TWO_SEATS_CENTS, TripFixtures.DECLINING, TripFixtures.BEFORE_DEPARTURE));
        assertEquals(TripError.PAYMENT_DECLINED, error.error());
        assertFalse(reservation.isPaid());
    }

    @Test
    void reservationIsPaidOnlyOnce() {
        var reservation = TripFixtures.reservation(TripFixtures.stationTrip(), 2);
        reservation.pay(TWO_SEATS_CENTS, TripFixtures.ACCEPTING, TripFixtures.BEFORE_DEPARTURE);
        var error = assertThrows(TripException.class,
                () -> reservation.pay(TWO_SEATS_CENTS, TripFixtures.ACCEPTING, TripFixtures.BEFORE_DEPARTURE));
        assertEquals(TripError.ALREADY_PAID, error.error());
    }

    @Test
    void polytechDonationIsFreeAndOptional() {
        var trip = TripFixtures.polytechTrip();
        var reservation = TripFixtures.reservation(trip, 1);
        var donation = reservation.pay(300, TripFixtures.ACCEPTING, TripFixtures.BEFORE_DEPARTURE);
        assertEquals(PaymentType.DONATION, donation.type());
        assertTrue(reservation.isPaid());
        reservation.validate(trip.driver());
    }

    @Test
    void donationMustBePositive() {
        var reservation = TripFixtures.reservation(TripFixtures.polytechTrip(), 1);
        var error = assertThrows(TripException.class,
                () -> reservation.pay(0, TripFixtures.ACCEPTING, TripFixtures.BEFORE_DEPARTURE));
        assertEquals(TripError.INVALID_AMOUNT, error.error());
    }

    @Test
    void refusedReservationCannotBePaid() {
        var trip = TripFixtures.stationTrip();
        var reservation = TripFixtures.reservation(trip, 2);
        reservation.refuse(trip.driver());
        var error = assertThrows(TripException.class,
                () -> reservation.pay(TWO_SEATS_CENTS, TripFixtures.ACCEPTING, TripFixtures.BEFORE_DEPARTURE));
        assertEquals(TripError.RESERVATION_CLOSED, error.error());
    }
}
