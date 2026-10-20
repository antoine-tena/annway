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
import java.util.stream.IntStream;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

/**
 * Le contrat de {@link TripFilterContractTest} sur des listes générées : un filtre ne renvoie
 * jamais plus de trajets qu'il n'en reçoit, n'en ajoute aucun et garde leur ordre.
 */
class TripFilterPropertiesTest {

    static final int MAX_TRIPS = 20;
    static final long MAX_PRICE_CENTS = 5000;
    static final int MINUTES_PER_DAY = 24 * 60;

    @Property
    void neverReturnsMoreTripsThanReceived(@ForAll("filters") TripFilter filter, @ForAll("trips") List<Trip> trips) {
        assertTrue(filter.filter(trips).size() <= trips.size());
    }

    @Property
    void neverAddsTripAndKeepsOrder(@ForAll("filters") TripFilter filter, @ForAll("trips") List<Trip> trips) {
        var receivedIds = ids(trips);
        var keptIds = ids(filter.filter(trips));
        var keptInReceivedOrder = receivedIds.stream().filter(keptIds::contains).toList();
        assertEquals(keptInReceivedOrder, keptIds);
    }

    @Provide
    Arbitrary<TripFilter> filters() {
        var priceFilters = Arbitraries.longs().between(0, MAX_PRICE_CENTS).map(PriceFilter::new);
        var timeWindowFilters = Combinators.combine(times(), times())
                .as((first, second) -> first.isAfter(second)
                        ? new TimeWindowFilter(second, first)
                        : new TimeWindowFilter(first, second));
        return Arbitraries.oneOf(Arbitraries.just(new NoFilter()), priceFilters, timeWindowFilters);
    }

    @Provide
    Arbitrary<List<Trip>> trips() {
        var priceOrDonation = Arbitraries.longs().between(0, MAX_PRICE_CENTS);
        var departureMinute = Arbitraries.integers().between(0, MINUTES_PER_DAY - 1);
        return Combinators.combine(priceOrDonation, departureMinute).as(TripShape::new)
                .list().ofMaxSize(MAX_TRIPS)
                .map(shapes -> IntStream.range(0, shapes.size()).mapToObj(index -> shapes.get(index).trip(index + 1)).toList());
    }

    private static Arbitrary<LocalTime> times() {
        return Arbitraries.integers().between(0, MINUTES_PER_DAY - 1).map(minute -> LocalTime.MIN.plusMinutes(minute));
    }

    /** Un trajet généré : un prix par place, ou 0 pour un trajet Polytech à don libre. */
    private record TripShape(long priceCents, int departureMinute) {

        Trip trip(int id) {
            var departureAt = MORNING.plusSeconds(departureMinute * 60L);
            return priceCents == 0 ? polytechTrip(id, departureAt) : stationTrip(id, departureAt, priceCents);
        }
    }
}
