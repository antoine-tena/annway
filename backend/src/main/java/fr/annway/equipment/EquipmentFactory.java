package fr.annway.equipment;

import java.util.Objects;

/** Fabrique simple du matériel : seul point de création d'un {@link Equipment}. */
public final class EquipmentFactory {

    private EquipmentFactory() {
    }

    public static Equipment create(EquipmentType type, String description) {
        Objects.requireNonNull(type, "type");
        return switch (type) {
            case SKI -> new Ski(description);
            case SNOWBOARD -> new Snowboard(description);
            case SLED -> new Sled(description);
        };
    }
}
