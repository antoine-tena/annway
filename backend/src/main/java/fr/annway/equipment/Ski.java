package fr.annway.equipment;

/** Paire de skis. */
public final class Ski extends Equipment {

    static final int SKI_BULK = 1;

    Ski(String description) {
        super(description);
    }

    @Override
    public EquipmentType type() {
        return EquipmentType.SKI;
    }

    @Override
    public int bulk() {
        return SKI_BULK;
    }
}
