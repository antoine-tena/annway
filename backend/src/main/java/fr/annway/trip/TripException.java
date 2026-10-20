package fr.annway.trip;

import java.util.Objects;

/** Erreur métier du domaine des trajets ([DOMAIN-ERRORS]) : code machine et message en français. */
public final class TripException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final TripError error;

    public TripException(TripError error) {
        super(Objects.requireNonNull(error, "error").userMessage());
        this.error = error;
    }

    public TripError error() {
        return error;
    }

    /** Code machine stable, en `UPPER_SNAKE_CASE`. */
    public String code() {
        return error.name();
    }

    /** Lève {@link TripError#BLANK_FIELD} si le texte est absent ou vide, sinon le renvoie épuré. */
    static String requireNotBlank(String text, String fieldName) {
        Objects.requireNonNull(text, fieldName);
        if (text.isBlank()) {
            throw new TripException(TripError.BLANK_FIELD);
        }
        return text.strip();
    }
}
