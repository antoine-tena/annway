package fr.annway.search;

import fr.annway.account.Driver;
import fr.annway.account.Vehicle;
import fr.annway.trip.PolytechTrip;
import fr.annway.trip.StationTrip;
import fr.annway.trip.Trip;
import fr.annway.trip.TripRoute;
import java.time.Instant;
import java.util.List;

/** Données de test fictives de la recherche de trajets. */
final class SearchFixtures {

    /** 7 h 30 UTC le 15 janvier : 8 h 30 à Paris (heure d'hiver). */
    static final Instant MORNING = Instant.parse("2027-01-15T07:30:00Z");

    private SearchFixtures() {
    }

    static Driver driver() {
        return new Driver(1, "Alan", "Turing", "alan@example.org", "0611111111",
                List.of(new Vehicle("AB-123-CD", "Clio", 3, 4)));
    }

    static StationTrip stationTrip(int id, Instant departureAt, long pricePerSeatCents) {
        var driver = driver();
        return new StationTrip(new TripRoute(id, "Grenoble", "Chamrousse", departureAt), driver,
                driver.vehicles().getFirst(), pricePerSeatCents);
    }

    static PolytechTrip polytechTrip(int id, Instant departureAt) {
        var driver = driver();
        return new PolytechTrip(new TripRoute(id, "Grenoble", "Polytech", departureAt), driver,
                driver.vehicles().getFirst(), departureAt.plusSeconds(3600), "Chamrousse");
    }

    static List<Integer> ids(List<Trip> trips) {
        return trips.stream().map(Trip::id).toList();
    }
}
