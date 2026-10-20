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

class PriceFilterTest {

    @Test
    void keepsTripsUpToMaxPriceIncluded() {
        List<Trip> trips = List.of(stationTrip(1, MORNING, 1499), stationTrip(2, MORNING, 1500),
                stationTrip(3, MORNING, 1501));
        assertEquals(List.of(1, 2), ids(new PriceFilter(1500).filter(trips)));
    }

    @Test
    void alwaysKeepsPolytechTripsWithFreeDonation() {
        List<Trip> trips = List.of(stationTrip(1, MORNING, 1500), polytechTrip(2, MORNING));
        assertEquals(List.of(2), ids(new PriceFilter(0).filter(trips)));
    }

    @Test
    void rejectsNegativeMaxPrice() {
        var error = assertThrows(SearchException.class, () -> new PriceFilter(-1));
        assertEquals(SearchError.NEGATIVE_MAX_PRICE, error.error());
        assertEquals("NEGATIVE_MAX_PRICE", error.code());
    }

    @Test
    void rejectsMissingList() {
        assertThrows(NullPointerException.class, () -> new PriceFilter(1500).filter(null));
    }
}
