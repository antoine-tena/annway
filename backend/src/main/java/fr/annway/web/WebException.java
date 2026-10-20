package fr.annway.web;

import java.util.Objects;
import java.util.Optional;

/**
 * Erreur de l'API web ([DOMAIN-ERRORS]) : code machine, statut HTTP et message en français ;
 * le champ fautif, s'il y en a un, pour que l'interface le signale.
 */
public final class WebException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final WebError error;
    private final String field;

    public WebException(WebError error) {
        this(error, "");
    }

    public WebException(WebError error, String field) {
        super(Objects.requireNonNull(error, "error").userMessage());
        this.error = error;
        this.field = Objects.requireNonNull(field, "field");
    }

    public WebError error() {
        return error;
    }

    public String code() {
        return error.name();
    }

    public Optional<String> field() {
        return field.isEmpty() ? Optional.empty() : Optional.of(field);
    }

    static WebException invalidField(String field) {
        return new WebException(WebError.INVALID_FIELD, field);
    }
}
