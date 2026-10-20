package fr.annway.web;

import fr.annway.trip.SearchCriteria;
import fr.annway.trip.TripType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Instant;

/**
 * Lit les critères de recherche et les dates d'un formulaire. Une heure saisie est une heure de
 * Paris, convertie en instant UTC ([TIME-ZONE]).
 */
public final class CriteriaForm {

    public static final ZoneId PARIS = ZoneId.of("Europe/Paris");

    private CriteriaForm() {
    }

    /** Vrai si le formulaire porte les trois critères obligatoires d'une recherche. */
    public static boolean hasCriteria(FormData form) {
        return form.optionalText("departure").isPresent()
                && form.optionalText("arrival").isPresent()
                && form.optionalText("date").isPresent();
    }

    /** Départ, arrivée, date, nombre de passagers (un par défaut) et type de trajet facultatif. */
    public static SearchCriteria criteria(FormData form) {
        return new SearchCriteria(form.text("departure"), form.text("arrival"), form.date("date"),
                form.optionalInteger("passengerCount").orElse(1),
                form.optionalEnum("type", TripType.class));
    }

    public static Instant parisInstant(FormData form, String name) {
        LocalDateTime local = form.dateTime(name);
        return local.atZone(PARIS).toInstant();
    }
}
