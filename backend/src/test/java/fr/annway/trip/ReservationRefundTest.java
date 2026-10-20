package fr.annway.trip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ReservationRefundTest {

    private static final long ONE_SEAT_CENTS = TripFixtures.PRICE_PER_SEAT_CENTS;
    private static final Clock LAST_FREE_CANCELLATION = Clock.fixed(
            TripFixtures.LATE_EVENING.minus(Reservation.FREE_CANCELLATION_NOTICE), ZoneOffset.UTC);
    private static final Clock TOO_LATE = Clock.fixed(
            TripFixtures.LATE_EVENING.minus(Reservation.FREE_CANCELLATION_NOTICE).plusSeconds(1), ZoneOffset.UTC);

    private static Reservation paidStationReservation() {
        var reservation = TripFixtures.reservation(TripFixtures.stationTrip(), 1);
        reservation.pay(ONE_SEAT_CENTS, TripFixtures.ACCEPTING, TripFixtures.BEFORE_DEPARTURE);
        return reservation;
    }

    @Test
    void refusedPaidReservationIsRefundedInFull() {
        var reservation = paidStationReservation();
        reservation.refuse(reservation.trip().driver());
        var refund = reservation.refund(TripFixtures.REFUNDING, TripFixtures.BEFORE_DEPARTURE);
        assertEquals(Optional.of(new Refund(ONE_SEAT_CENTS, TripFixtures.BEFORE_DEPARTURE.instant())), refund);
        assertEquals(0, reservation.refundDueCents());
    }

    @Test
    void passengerCancellingUpToTheNoticeIsRefunded() {
        var reservation = paidStationReservation();
        reservation.cancel(reservation.passenger(), LAST_FREE_CANCELLATION);
        assertEquals(ONE_SEAT_CENTS, reservation.refundDueCents());
        assertTrue(reservation.refund(TripFixtures.REFUNDING, LAST_FREE_CANCELLATION).isPresent());
    }

    @Test
    void passengerCancellingTooLateIsNotRefunded() {
        var reservation = paidStationReservation();
        reservation.cancel(reservation.passenger(), TOO_LATE);
        assertEquals(0, reservation.refundDueCents());
        assertEquals(Optional.empty(), reservation.refund(TripFixtures.REFUNDING, TOO_LATE));
    }

    @Test
    void donationFollowsTheSameRule() {
        var reservation = TripFixtures.reservation(TripFixtures.polytechTrip(), 1);
        reservation.pay(300, TripFixtures.ACCEPTING, TripFixtures.BEFORE_DEPARTURE);
        reservation.cancel(reservation.passenger(), TripFixtures.BEFORE_DEPARTURE);
        assertEquals(300, reservation.refundDueCents());
    }

    @Test
    void unpaidReservationHasNothingToRefund() {
        var reservation = TripFixtures.reservation(TripFixtures.stationTrip(), 1);
        reservation.refuse(reservation.trip().driver());
        assertEquals(Optional.empty(), reservation.refund(TripFixtures.REFUNDING, TripFixtures.BEFORE_DEPARTURE));
    }

    @Test
    void cancelledTripClosesOpenReservationsWithFullRefund() {
        var reservation = paidStationReservation();
        reservation.validate(reservation.trip().driver());
        reservation.trip().cancel();
        reservation.closeForCancelledTrip();
        assertEquals(ReservationStatus.TRIP_CANCELLED, reservation.status());
        assertEquals(ONE_SEAT_CENTS, reservation.refundDueCents());
    }

    @Test
    void closedReservationStaysAsItIsWhenTheTripIsCancelled() {
        var reservation = TripFixtures.reservation(TripFixtures.stationTrip(), 1);
        reservation.cancel(reservation.passenger(), TOO_LATE);
        reservation.trip().cancel();
        reservation.closeForCancelledTrip();
        assertEquals(ReservationStatus.CANCELLED, reservation.status());
    }

    @Test
    void openTripDoesNotCloseItsReservations() {
        var reservation = TripFixtures.reservation(TripFixtures.stationTrip(), 1);
        var error = assertThrows(TripException.class, reservation::closeForCancelledTrip);
        assertEquals(TripError.TRIP_NOT_CANCELLED, error.error());
    }

    @Test
    void declinedRefundStaysDueAndIsNotRecorded() {
        var reservation = paidStationReservation();
        reservation.refuse(reservation.trip().driver());
        var error = assertThrows(TripException.class,
                () -> reservation.refund(TripFixtures.REFUSING_REFUNDS, TripFixtures.BEFORE_DEPARTURE));
        assertEquals(TripError.REFUND_DECLINED, error.error());
        assertEquals(ONE_SEAT_CENTS, reservation.refundDueCents());
        assertTrue(reservation.refund().isEmpty());
    }

    @Test
    void refundHappensOnlyOnce() {
        var reservation = paidStationReservation();
        reservation.refuse(reservation.trip().driver());
        reservation.refund(TripFixtures.REFUNDING, TripFixtures.BEFORE_DEPARTURE);
        assertEquals(Optional.empty(), reservation.refund(TripFixtures.REFUNDING, TripFixtures.BEFORE_DEPARTURE));
    }
}
