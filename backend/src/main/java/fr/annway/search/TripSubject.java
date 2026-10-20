package fr.annway.search;

import fr.annway.trip.Trip;

/** Sujet observé : prévient ses observateurs à chaque nouveau trajet. */
public interface TripSubject {

    /** Abonne un observateur ; l'abonner une seconde fois ne change rien. */
    void subscribe(TripObserver observer);

    void unsubscribe(TripObserver observer);

    /** Prévient chaque observateur abonné, une fois, du trajet donné. */
    void notifyObservers(Trip trip);
}
