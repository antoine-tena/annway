package fr.annway.search;

import fr.annway.trip.Trip;

/** Observateur des nouveaux trajets d'un {@link TripSubject}. */
public interface TripObserver {

    void update(Trip trip);
}
