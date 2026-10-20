package fr.annway.search;

import fr.annway.account.Driver;
import fr.annway.trip.SearchCriteria;
import fr.annway.trip.Trip;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Catalogue des trajets publiés. Il prévient ses abonnés de chaque trajet ajouté, et sa recherche
 * garde les trajets qui répondent aux critères avant d'appliquer le filtre choisi, aucun par défaut.
 */
public final class TripCatalogue implements TripSubject {

    private final List<Trip> trips = new ArrayList<>();
    private final Set<TripObserver> observers = new LinkedHashSet<>();
    private TripFilter filter = new NoFilter();

    public List<Trip> trips() {
        return List.copyOf(trips);
    }

    /** Ajoute un trajet au catalogue et en prévient les abonnés. */
    public void addTrip(Trip trip) {
        trips.add(Objects.requireNonNull(trip, "trip"));
        notifyObservers(trip);
    }

    /**
     * Publie le trajet d'un conducteur : seul son conducteur le publie, une seule fois. Les abonnés
     * en sont prévenus comme pour {@link #addTrip}.
     */
    public void publish(Driver driver, Trip trip) {
        Objects.requireNonNull(driver, "driver");
        Objects.requireNonNull(trip, "trip");
        if (driver.id() != trip.driver().id()) {
            throw new SearchException(SearchError.NOT_TRIP_DRIVER);
        }
        if (trips.stream().anyMatch(published -> published.id() == trip.id())) {
            throw new SearchException(SearchError.TRIP_ALREADY_PUBLISHED);
        }
        addTrip(trip);
    }

    /** Change le filtre appliqué aux recherches suivantes. */
    public void setFilter(TripFilter filter) {
        this.filter = Objects.requireNonNull(filter, "filter");
    }

    /** Trajets qui répondent aux critères, dans l'ordre d'ajout, puis passés au filtre courant. */
    public List<Trip> search(SearchCriteria criteria) {
        Objects.requireNonNull(criteria, "criteria");
        return filter.filter(trips.stream().filter(trip -> trip.matches(criteria)).toList());
    }

    @Override
    public void subscribe(TripObserver observer) {
        observers.add(Objects.requireNonNull(observer, "observer"));
    }

    @Override
    public void unsubscribe(TripObserver observer) {
        observers.remove(Objects.requireNonNull(observer, "observer"));
    }

    @Override
    public void notifyObservers(Trip trip) {
        Objects.requireNonNull(trip, "trip");
        List.copyOf(observers).forEach(observer -> observer.update(trip));
    }
}
