package fr.annway.search;

import fr.annway.trip.Trip;
import java.util.List;
import java.util.Objects;

/** Filtre par défaut : la recherche sans filtre renvoie tous les trajets reçus, dans le même ordre. */
public final class NoFilter implements TripFilter {

    @Override
    public List<Trip> filter(List<Trip> trips) {
        return List.copyOf(Objects.requireNonNull(trips, "trips"));
    }
}
