package fr.annway.search;

import static fr.annway.search.SearchFixtures.MORNING;
import static fr.annway.search.SearchFixtures.TO_CHAMROUSSE;
import static fr.annway.search.SearchFixtures.passenger;
import static fr.annway.search.SearchFixtures.polytechTrip;
import static fr.annway.search.SearchFixtures.stationTrip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class TripAlertTest {

    private final TripAlert alert = new TripAlert(passenger(), TO_CHAMROUSSE);

    @Test
    void activeAlertKeepsMatchingTrip() {
        var trip = stationTrip(1, MORNING, 1500);
        alert.update(trip);
        assertTrue(alert.isActive());
        assertEquals(List.of(trip), alert.matchedTrips());
    }

    @Test
    void ignoresTripThatDoesNotMatch() {
        alert.update(polytechTrip(1, MORNING));
        alert.update(stationTrip(2, MORNING.plusSeconds(86_400), 1500));
        assertEquals(List.of(), alert.matchedTrips());
    }

    @Test
    void switchedOffAlertIgnoresTripsUntilSwitchedOnAgain() {
        alert.deactivate();
        alert.update(stationTrip(1, MORNING, 1500));
        assertFalse(alert.isActive());
        assertEquals(List.of(), alert.matchedTrips());
        alert.activate();
        var trip = stationTrip(2, MORNING, 1500);
        alert.update(trip);
        assertEquals(List.of(trip), alert.matchedTrips());
    }

    @Test
    void subscribedAlertIsWarnedOfTripAddedToCatalogue() {
        var catalogue = new TripCatalogue();
        catalogue.subscribe(alert);
        var trip = stationTrip(1, MORNING, 1500);
        catalogue.addTrip(trip);
        catalogue.addTrip(polytechTrip(2, MORNING));
        assertEquals(List.of(trip), alert.matchedTrips());
    }

    @Test
    void keepsPassengerAndCriteria() {
        assertEquals(10, alert.passenger().id());
        assertEquals(TO_CHAMROUSSE, alert.criteria());
    }

    @Test
    void rejectsMissingValues() {
        assertThrows(NullPointerException.class, () -> new TripAlert(null, TO_CHAMROUSSE));
        assertThrows(NullPointerException.class, () -> new TripAlert(passenger(), null));
        assertThrows(NullPointerException.class, () -> alert.update(null));
    }
}
