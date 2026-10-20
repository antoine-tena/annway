package fr.annway.search;

import static fr.annway.search.SearchFixtures.MORNING;
import static fr.annway.search.SearchFixtures.ids;
import static fr.annway.search.SearchFixtures.polytechTrip;
import static fr.annway.search.SearchFixtures.stationTrip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.annway.trip.Trip;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class TimeWindowFilterTest {

    private static final LocalTime EIGHT = LocalTime.of(8, 0);
    private static final LocalTime NINE = LocalTime.of(9, 0);

    @Test
    void keepsTripsLeavingWithinWindowBoundsIncluded() {
        List<Trip> trips = List.of(stationTrip(1, Instant.parse("2027-01-15T06:59:00Z"), 1500),
                stationTrip(2, Instant.parse("2027-01-15T07:00:00Z"), 1500),
                polytechTrip(3, MORNING),
                stationTrip(4, Instant.parse("2027-01-15T08:00:00Z"), 1500),
                stationTrip(5, Instant.parse("2027-01-15T08:01:00Z"), 1500));
        assertEquals(List.of(2, 3, 4), ids(new TimeWindowFilter(EIGHT, NINE).filter(trips)));
    }

    @Test
    void readsDepartureInParisSummerTime() {
        List<Trip> trips = List.of(stationTrip(1, Instant.parse("2027-07-15T06:30:00Z"), 1500),
                stationTrip(2, Instant.parse("2027-07-15T07:30:00Z"), 1500));
        assertEquals(List.of(1), ids(new TimeWindowFilter(EIGHT, NINE).filter(trips)));
    }

    @Test
    void acceptsSingleInstantWindow() {
        List<Trip> trips = List.of(polytechTrip(1, MORNING));
        var halfPastEight = LocalTime.of(8, 30);
        assertEquals(List.of(1), ids(new TimeWindowFilter(halfPastEight, halfPastEight).filter(trips)));
    }

    @Test
    void rejectsInvertedWindow() {
        var error = assertThrows(SearchException.class, () -> new TimeWindowFilter(NINE, EIGHT));
        assertEquals(SearchError.INVERTED_TIME_WINDOW, error.error());
    }

    @Test
    void rejectsMissingBounds() {
        assertThrows(NullPointerException.class, () -> new TimeWindowFilter(null, NINE));
        assertThrows(NullPointerException.class, () -> new TimeWindowFilter(EIGHT, null));
    }
}
