package fr.annway.search;

import static fr.annway.search.SearchFixtures.MORNING;
import static fr.annway.search.SearchFixtures.ids;
import static fr.annway.search.SearchFixtures.polytechTrip;
import static fr.annway.search.SearchFixtures.stationTrip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.annway.trip.Trip;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** Ce que tout filtre garantit : il ne fait que retirer des trajets, dans l'ordre reçu. */
class TripFilterContractTest {

    static Stream<TripFilter> filters() {
        return Stream.of(new NoFilter(), new PriceFilter(1500),
                new TimeWindowFilter(LocalTime.of(8, 0), LocalTime.of(9, 0)));
    }

    private static List<Trip> trips() {
        return List.of(stationTrip(4, MORNING, 2000), polytechTrip(2, MORNING),
                stationTrip(9, MORNING.plusSeconds(7200), 1000), stationTrip(1, MORNING, 1500));
    }

    @ParameterizedTest
    @MethodSource("filters")
    void emptyListGivesEmptyList(TripFilter filter) {
        assertEquals(List.of(), filter.filter(List.of()));
    }

    @ParameterizedTest
    @MethodSource("filters")
    void neverAddsTripAndKeepsOrder(TripFilter filter) {
        var receivedIds = ids(trips());
        var keptIds = ids(filter.filter(trips()));
        assertTrue(receivedIds.containsAll(keptIds));
        var keptInReceivedOrder = receivedIds.stream().filter(keptIds::contains).toList();
        assertEquals(keptInReceivedOrder, keptIds);
    }
}
