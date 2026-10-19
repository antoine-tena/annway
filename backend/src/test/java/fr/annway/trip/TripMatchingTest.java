package fr.annway.trip;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TripMatchingTest {

    /** Le départ, 23 h 30 UTC le 14, tombe le 15 janvier à Paris. */
    private static final LocalDate PARIS_DAY = LocalDate.of(2027, 1, 15);

    @Test
    void matchesSamePlacesIgnoringCaseOnTheParisDay() {
        var criteria = new SearchCriteria("grenoble", "CHAMROUSSE", PARIS_DAY, 2);
        assertTrue(TripFixtures.stationTrip().matches(criteria));
    }

    @Test
    void doesNotMatchTheUtcDay() {
        var criteria = new SearchCriteria("Grenoble", "Chamrousse", PARIS_DAY.minusDays(1), 1);
        assertFalse(TripFixtures.stationTrip().matches(criteria));
    }

    @Test
    void doesNotMatchAnotherArrival() {
        var criteria = new SearchCriteria("Grenoble", "Les 7 Laux", PARIS_DAY, 1);
        assertFalse(TripFixtures.stationTrip().matches(criteria));
    }

    @Test
    void doesNotMatchTooManyPassengers() {
        var criteria = new SearchCriteria("Grenoble", "Chamrousse", PARIS_DAY, 4);
        assertFalse(TripFixtures.stationTrip().matches(criteria));
    }

    @Test
    void matchesOnlyTheWantedType() {
        var station = new SearchCriteria("Grenoble", "Polytech", PARIS_DAY, 1, Optional.of(TripType.STATION));
        var polytech = new SearchCriteria("Grenoble", "Polytech", PARIS_DAY, 1, Optional.of(TripType.POLYTECH));
        assertFalse(TripFixtures.polytechTrip().matches(station));
        assertTrue(TripFixtures.polytechTrip().matches(polytech));
    }

    @Test
    void cancelledTripNeverMatches() {
        var trip = TripFixtures.stationTrip();
        trip.cancel();
        assertFalse(trip.matches(new SearchCriteria("Grenoble", "Chamrousse", PARIS_DAY, 1)));
    }
}
