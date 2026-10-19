package fr.annway.trip;

import java.time.Instant;
import java.util.Objects;

/** Ce qui définit un trajet sur la carte : identifiant, départ, arrivée et instant de départ (UTC). */
public record TripRoute(int id, String departure, String arrival, Instant departureAt) {

    public TripRoute {
        if (id < 1) {
            throw new TripException(TripError.INVALID_TRIP_ID);
        }
        departure = TripException.requireNotBlank(departure, "departure");
        arrival = TripException.requireNotBlank(arrival, "arrival");
        Objects.requireNonNull(departureAt, "departureAt");
        if (departure.equalsIgnoreCase(arrival)) {
            throw new TripException(TripError.SAME_DEPARTURE_AND_ARRIVAL);
        }
    }
}
