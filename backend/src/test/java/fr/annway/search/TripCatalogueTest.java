package fr.annway.search;

import static fr.annway.search.SearchFixtures.MORNING;
import static fr.annway.search.SearchFixtures.TO_CHAMROUSSE;
import static fr.annway.search.SearchFixtures.ids;
import static fr.annway.search.SearchFixtures.polytechTrip;
import static fr.annway.search.SearchFixtures.stationTrip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.annway.trip.Trip;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TripCatalogueTest {

    private final TripCatalogue catalogue = new TripCatalogue();
    private final List<Trip> notifiedTrips = new ArrayList<>();
    private final TripObserver recorder = notifiedTrips::add;

    @Test
    void searchReturnsMatchingTripsInAddingOrder() {
        catalogue.addTrip(stationTrip(3, MORNING, 2000));
        catalogue.addTrip(polytechTrip(1, MORNING));
        catalogue.addTrip(stationTrip(2, MORNING, 1000));
        assertEquals(List.of(3, 2), ids(catalogue.search(TO_CHAMROUSSE)));
    }

    @Test
    void searchAppliesCurrentFilterAndChangingItChangesNextResults() {
        catalogue.addTrip(stationTrip(1, MORNING, 2000));
        catalogue.addTrip(stationTrip(2, MORNING, 1000));
        catalogue.setFilter(new PriceFilter(1500));
        assertEquals(List.of(2), ids(catalogue.search(TO_CHAMROUSSE)));
        catalogue.setFilter(new NoFilter());
        assertEquals(List.of(1, 2), ids(catalogue.search(TO_CHAMROUSSE)));
    }

    @Test
    void cancelledTripIsLeftOutOfSearch() {
        var cancelled = stationTrip(1, MORNING, 1500);
        catalogue.addTrip(cancelled);
        cancelled.cancel();
        assertEquals(List.of(), catalogue.search(TO_CHAMROUSSE));
    }

    @Test
    void addingTripNotifiesEverySubscribedObserverOnceWithThatTrip() {
        var otherTrips = new ArrayList<Trip>();
        catalogue.subscribe(recorder);
        catalogue.subscribe(otherTrips::add);
        var trip = stationTrip(1, MORNING, 1500);
        catalogue.addTrip(trip);
        assertEquals(List.of(trip), notifiedTrips);
        assertEquals(List.of(trip), otherTrips);
        assertEquals(List.of(trip), catalogue.trips());
    }

    @Test
    void unsubscribedObserverIsNoLongerNotified() {
        catalogue.subscribe(recorder);
        catalogue.unsubscribe(recorder);
        catalogue.addTrip(stationTrip(1, MORNING, 1500));
        assertEquals(List.of(), notifiedTrips);
    }

    @Test
    void subscribingTwiceNotifiesOnce() {
        catalogue.subscribe(recorder);
        catalogue.subscribe(recorder);
        catalogue.addTrip(stationTrip(1, MORNING, 1500));
        assertEquals(1, notifiedTrips.size());
    }

    @Test
    void rejectsMissingValues() {
        assertThrows(NullPointerException.class, () -> catalogue.addTrip(null));
        assertThrows(NullPointerException.class, () -> catalogue.setFilter(null));
        assertThrows(NullPointerException.class, () -> catalogue.search(null));
        assertThrows(NullPointerException.class, () -> catalogue.subscribe(null));
        assertThrows(NullPointerException.class, () -> catalogue.unsubscribe(null));
        assertThrows(NullPointerException.class, () -> catalogue.notifyObservers(null));
    }
}
