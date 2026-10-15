package fr.annway.equipment;

/** Snowboard. */
public final class Snowboard extends Equipment {

    static final int SNOWBOARD_BULK = 1;

    Snowboard(String description) {
        super(description);
    }

    @Override
    public EquipmentType type() {
        return EquipmentType.SNOWBOARD;
    }

    @Override
    public int bulk() {
        return SNOWBOARD_BULK;
    }
}
