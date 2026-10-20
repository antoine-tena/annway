package fr.annway.search;

import fr.annway.account.Passenger;
import fr.annway.trip.SearchCriteria;
import fr.annway.trip.Trip;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Alerte d'un passager sur ses critères de voyage. Tant qu'elle est active, elle garde chaque
 * nouveau trajet qui y répond ({@link #matchedTrips()}) : c'est ainsi que le passager est prévenu.
 */
public final class TripAlert implements TripObserver {

    private final Passenger passenger;
    private final SearchCriteria criteria;
    private final List<Trip> matchedTrips = new ArrayList<>();
    private boolean isActive = true;

    public TripAlert(Passenger passenger, SearchCriteria criteria) {
        this.passenger = Objects.requireNonNull(passenger, "passenger");
        this.criteria = Objects.requireNonNull(criteria, "criteria");
    }

    public Passenger passenger() {
        return passenger;
    }

    public SearchCriteria criteria() {
        return criteria;
    }

    public boolean isActive() {
        return isActive;
    }

    /** Trajets trouvés depuis la création de l'alerte, dans l'ordre où ils sont arrivés. */
    public List<Trip> matchedTrips() {
        return List.copyOf(matchedTrips);
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }

    @Override
    public void update(Trip trip) {
        Objects.requireNonNull(trip, "trip");
        if (isActive && trip.matches(criteria)) {
            matchedTrips.add(trip);
        }
    }
}
