package fr.annway.search;

import java.util.Objects;

/** Erreur métier de la recherche de trajets ([DOMAIN-ERRORS]) : code machine et message en français. */
public final class SearchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final SearchError error;

    public SearchException(SearchError error) {
        super(Objects.requireNonNull(error, "error").userMessage());
        this.error = error;
    }

    public SearchError error() {
        return error;
    }

    /** Code machine stable, en `UPPER_SNAKE_CASE`. */
    public String code() {
        return error.name();
    }
}
