package fr.annway.trip;

import fr.annway.account.Driver;
import fr.annway.account.Vehicle;
import java.time.Instant;
import java.util.List;

/** Données de test fictives du domaine des trajets. */
final class TripFixtures {

    /** 23 h 30 UTC le 14 janvier : déjà le 15 janvier à Paris. */
    static final Instant LATE_EVENING = Instant.parse("2027-01-14T23:30:00Z");
    static final long PRICE_PER_SEAT_CENTS = 1500;

    private TripFixtures() {
    }

    static Vehicle vehicle() {
        return new Vehicle("AB-123-CD", "Clio", 3, 4);
    }

    static Driver driver() {
        return new Driver(1, "Alan", "Turing", "alan@example.org", "0611111111", List.of(vehicle()));
    }

    static TripRoute route() {
        return new TripRoute(1, "Grenoble", "Chamrousse", LATE_EVENING);
    }

    static StationTrip stationTrip() {
        var driver = driver();
        return new StationTrip(route(), driver, driver.vehicles().getFirst(), PRICE_PER_SEAT_CENTS);
    }

    static PolytechTrip polytechTrip() {
        var driver = driver();
        return new PolytechTrip(new TripRoute(2, "Grenoble", "Polytech", LATE_EVENING), driver,
                driver.vehicles().getFirst(), LATE_EVENING.plusSeconds(3600), "Chamrousse");
    }
}
