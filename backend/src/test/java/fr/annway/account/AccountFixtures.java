package fr.annway.account;

import fr.annway.trip.SearchCriteria;
import fr.annway.trip.StationTrip;
import fr.annway.trip.TripRoute;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

/** Données de test fictives du domaine des comptes. */
final class AccountFixtures {

    /** 23 h 30 UTC le 14 janvier : déjà le 15 janvier à Paris. */
    static final Clock LATE_EVENING_CLOCK = Clock.fixed(Instant.parse("2027-01-14T23:30:00Z"), ZoneOffset.UTC);

    static final long PRICE_PER_SEAT_CENTS = 1500;

    /** Grenoble → Chamrousse le 15 janvier à Paris, pour un passager. */
    static final SearchCriteria TO_CHAMROUSSE =
            new SearchCriteria("Grenoble", "Chamrousse", LocalDate.of(2027, 1, 15), 1);

    private AccountFixtures() {
    }

    static Vehicle vehicle(String registration) {
        return new Vehicle(registration, "Clio", 4, 3);
    }

    static Passenger passenger(int id) {
        return new Passenger(id, "Ada", "Lovelace", "ada@example.org", "0600000000");
    }

    static Driver driver(int id) {
        return new Driver(id, "Alan", "Turing", "alan@example.org", "0611111111", List.of(vehicle("AB-123-CD")));
    }

    /** Trajet vers Chamrousse au départ du 15 janvier à Paris, avec le premier véhicule du conducteur. */
    static StationTrip stationTrip(int id, Driver driver) {
        var route = new TripRoute(id, "Grenoble", "Chamrousse", LATE_EVENING_CLOCK.instant());
        return new StationTrip(route, driver, driver.vehicles().getFirst(), PRICE_PER_SEAT_CENTS);
    }
}
