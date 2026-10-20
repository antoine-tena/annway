package fr.annway.search;

import fr.annway.trip.Trip;
import java.util.List;

/**
 * Stratégie de filtrage des résultats d'une recherche. Un filtre ne fait que retirer des trajets :
 * il n'en ajoute jamais et garde l'ordre de ceux qu'il reçoit.
 */
public interface TripFilter {

    List<Trip> filter(List<Trip> trips);
}
