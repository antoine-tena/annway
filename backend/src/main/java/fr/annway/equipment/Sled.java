package fr.annway.equipment;

/** Luge, plus encombrante qu'une paire de skis. */
public final class Sled extends Equipment {

    static final int SLED_BULK = 2;

    Sled(String description) {
        super(description);
    }

    @Override
    public EquipmentType type() {
        return EquipmentType.SLED;
    }

    @Override
    public int bulk() {
        return SLED_BULK;
    }
}
