package fr.annway.search;

import static fr.annway.search.SearchFixtures.MORNING;
import static fr.annway.search.SearchFixtures.TO_CHAMROUSSE;
import static fr.annway.search.SearchFixtures.passenger;
import static fr.annway.search.SearchFixtures.stationTrip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.annway.account.Driver;
import fr.annway.account.Vehicle;
import java.util.List;
import org.junit.jupiter.api.Test;

class TripPublicationTest {

    private final TripCatalogue catalogue = new TripCatalogue();
    private final TripAlert alert = new TripAlert(passenger(), TO_CHAMROUSSE);

    @Test
    void driverPublishesTripAndSubscribedAlertIsWarned() {
        catalogue.subscribe(alert);
        var trip = stationTrip(1, MORNING, 1500);
        catalogue.publish(trip.driver(), trip);
        assertEquals(List.of(trip), catalogue.trips());
        assertEquals(List.of(trip), alert.matchedTrips());
    }

    @Test
    void anotherDriverCannotPublishTheTrip() {
        var otherDriver = new Driver(2, "Grace", "Hopper", "grace@example.org", "0622222222",
                List.of(new Vehicle("EF-456-GH", "Zoé", 4, 2)));
        var error = assertThrows(SearchException.class,
                () -> catalogue.publish(otherDriver, stationTrip(1, MORNING, 1500)));
        assertEquals(SearchError.NOT_TRIP_DRIVER, error.error());
        assertEquals(List.of(), catalogue.trips());
    }

    @Test
    void sameTripCannotBePublishedTwice() {
        catalogue.subscribe(alert);
        var trip = stationTrip(1, MORNING, 1500);
        catalogue.publish(trip.driver(), trip);
        var error = assertThrows(SearchException.class, () -> catalogue.publish(trip.driver(), trip));
        assertEquals(SearchError.TRIP_ALREADY_PUBLISHED, error.error());
        assertEquals(List.of(trip), alert.matchedTrips());
    }

    @Test
    void rejectsMissingValues() {
        var trip = stationTrip(1, MORNING, 1500);
        assertThrows(NullPointerException.class, () -> catalogue.publish(null, trip));
        assertThrows(NullPointerException.class, () -> catalogue.publish(trip.driver(), null));
    }
}
