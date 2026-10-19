package fr.annway.trip;

import java.time.Instant;
import java.util.Objects;

/** Étape d'un trajet : un lieu de passage et l'heure prévue, en UTC ([FUSEAU]). */
public record Stop(String place, Instant passingAt) {

    public Stop {
        place = TripException.requireNotBlank(place, "place");
        Objects.requireNonNull(passingAt, "passingAt");
    }
}
