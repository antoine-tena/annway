package fr.annway.trip;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Critères d'une recherche de trajet : départ, arrivée, jour du départ (calendrier de Paris),
 * nombre de passagers et, s'il est donné, le type de trajet voulu.
 */
public record SearchCriteria(String departure, String arrival, LocalDate date, int passengerCount,
        Optional<TripType> type) {

    public SearchCriteria {
        departure = TripException.requireNotBlank(departure, "departure");
        arrival = TripException.requireNotBlank(arrival, "arrival");
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(type, "type");
        if (passengerCount < 1) {
            throw new TripException(TripError.INVALID_PASSENGER_COUNT);
        }
    }

    /** Critères sans préférence de type de trajet. */
    public SearchCriteria(String departure, String arrival, LocalDate date, int passengerCount) {
        this(departure, arrival, date, passengerCount, Optional.empty());
    }
}
