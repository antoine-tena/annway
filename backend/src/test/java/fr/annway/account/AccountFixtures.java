package fr.annway.account;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

/** Données de test fictives du domaine des comptes. */
final class AccountFixtures {

    /** 23 h 30 UTC le 14 janvier : déjà le 15 janvier à Paris. */
    static final Clock LATE_EVENING_CLOCK = Clock.fixed(Instant.parse("2027-01-14T23:30:00Z"), ZoneOffset.UTC);

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
}
