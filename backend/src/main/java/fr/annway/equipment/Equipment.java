package fr.annway.equipment;

import java.util.Objects;

/**
 * Matériel qu'un passager déclare en réservant. Son encombrement se compte dans la capacité de
 * matériel du véhicule, en unités d'encombrement. Une instance s'obtient par
 * {@link EquipmentFactory#create(EquipmentType, String)}.
 */
public abstract sealed class Equipment permits Ski, Snowboard, Sled {

    private final String description;

    protected Equipment(String description) {
        Objects.requireNonNull(description, "description");
        if (description.isBlank()) {
            throw new EquipmentException(EquipmentError.BLANK_DESCRIPTION);
        }
        this.description = description.strip();
    }

    public String description() {
        return description;
    }

    public abstract EquipmentType type();

    /** Place occupée dans le véhicule, en unités d'encombrement. */
    public abstract int bulk();
}
