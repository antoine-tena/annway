package fr.annway.trip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class TripLifecycleTest {

    @Test
    void finishesOnceDeparted() {
        var trip = TripFixtures.stationTrip();
        trip.finish(TripFixtures.AFTER_DEPARTURE);
        assertEquals(TripStatus.FINISHED, trip.status());
    }

    @Test
    void cannotFinishBeforeDeparture() {
        var trip = TripFixtures.stationTrip();
        var error = assertThrows(TripException.class, () -> trip.finish(TripFixtures.BEFORE_DEPARTURE));
        assertEquals(TripError.TRIP_NOT_DEPARTED, error.error());
    }

    @Test
    void cancelledTripCannotFinish() {
        var trip = TripFixtures.stationTrip();
        trip.cancel();
        var error = assertThrows(TripException.class, () -> trip.finish(TripFixtures.AFTER_DEPARTURE));
        assertEquals(TripError.TRIP_CANCELLED, error.error());
    }

    @Test
    void finishedTripCannotBeCancelled() {
        var trip = TripFixtures.stationTrip();
        trip.finish(TripFixtures.AFTER_DEPARTURE);
        var error = assertThrows(TripException.class, trip::cancel);
        assertEquals(TripError.TRIP_FINISHED, error.error());
    }

    @Test
    void requestIsDatedOnTheParisDay() {
        var criteria = new SearchCriteria("Grenoble", "Chamrousse", LocalDate.of(2027, 1, 15), 1);
        var lateEvening = Clock.fixed(TripFixtures.LATE_EVENING, ZoneOffset.UTC);
        var request = TripRequest.create(TripFixtures.passenger(10), criteria, lateEvening);
        assertEquals(LocalDate.of(2027, 1, 15), request.createdOn());
        assertTrue(request.isSatisfiedBy(TripFixtures.stationTrip()));
    }
}
