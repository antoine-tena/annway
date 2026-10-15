package fr.annway.account;

import java.util.Objects;

/** Erreur métier du domaine des comptes ([ERREURS-METIER]) : code machine et message en français. */
public final class AccountException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final AccountError error;

    public AccountException(AccountError error) {
        super(Objects.requireNonNull(error, "error").userMessage());
        this.error = error;
    }

    public AccountError error() {
        return error;
    }

    /** Code machine stable, en `UPPER_SNAKE_CASE`. */
    public String code() {
        return error.name();
    }

    /** Lève {@link AccountError#BLANK_FIELD} si le texte est absent ou vide, sinon le renvoie épuré. */
    static String requireNotBlank(String text, String fieldName) {
        Objects.requireNonNull(text, fieldName);
        if (text.isBlank()) {
            throw new AccountException(AccountError.BLANK_FIELD);
        }
        return text.strip();
    }
}
