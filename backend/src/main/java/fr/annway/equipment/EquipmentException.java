package fr.annway.equipment;

import java.util.Objects;

/** Erreur métier du domaine du matériel ([DOMAIN-ERRORS]) : code machine et message en français. */
public final class EquipmentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final EquipmentError error;

    public EquipmentException(EquipmentError error) {
        super(Objects.requireNonNull(error, "error").userMessage());
        this.error = error;
    }

    public EquipmentError error() {
        return error;
    }

    /** Code machine stable, en `UPPER_SNAKE_CASE`. */
    public String code() {
        return error.name();
    }
}
