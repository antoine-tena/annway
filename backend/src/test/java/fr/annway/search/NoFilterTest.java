package fr.annway.search;

import static fr.annway.search.SearchFixtures.MORNING;
import static fr.annway.search.SearchFixtures.ids;
import static fr.annway.search.SearchFixtures.polytechTrip;
import static fr.annway.search.SearchFixtures.stationTrip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.annway.trip.Trip;
import java.util.List;
import org.junit.jupiter.api.Test;

class NoFilterTest {

    @Test
    void returnsEveryTripInSameOrder() {
        List<Trip> trips = List.of(stationTrip(3, MORNING, 2000), polytechTrip(1, MORNING), stationTrip(2, MORNING, 500));
        assertEquals(List.of(3, 1, 2), ids(new NoFilter().filter(trips)));
    }

    @Test
    void rejectsMissingList() {
        assertThrows(NullPointerException.class, () -> new NoFilter().filter(null));
    }
}
