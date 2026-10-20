package fr.annway.trip;

import fr.annway.account.Passenger;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Demande de trajet d'un passager qui n'a rien trouvé : ses critères et le jour où il l'a faite,
 * dans le calendrier de Paris ([TIME-ZONE]).
 */
public record TripRequest(Passenger passenger, SearchCriteria criteria, LocalDate createdOn) {

    public TripRequest {
        Objects.requireNonNull(passenger, "passenger");
        Objects.requireNonNull(criteria, "criteria");
        Objects.requireNonNull(createdOn, "createdOn");
    }

    public static TripRequest create(Passenger passenger, SearchCriteria criteria, Clock clock) {
        var createdOn = LocalDate.ofInstant(Objects.requireNonNull(clock, "clock").instant(), Trip.PARIS);
        return new TripRequest(passenger, criteria, createdOn);
    }

    /** Vrai si ce trajet répond à la demande. */
    public boolean isSatisfiedBy(Trip trip) {
        return trip.matches(criteria);
    }
}
