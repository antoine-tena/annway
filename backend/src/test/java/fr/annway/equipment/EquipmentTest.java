package fr.annway.equipment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class EquipmentTest {

    @Test
    void skiAndSnowboardTakeOneUnit() {
        assertEquals(1, EquipmentFactory.create(EquipmentType.SKI, "Rossignol 170").bulk());
        assertEquals(1, EquipmentFactory.create(EquipmentType.SNOWBOARD, "Burton").bulk());
    }

    @Test
    void sledTakesTwoUnits() {
        assertEquals(2, EquipmentFactory.create(EquipmentType.SLED, "Luge en bois").bulk());
    }

    @ParameterizedTest
    @EnumSource(EquipmentType.class)
    void everyEquipmentTakesSomeSpace(EquipmentType type) {
        assertTrue(EquipmentFactory.create(type, "Mon matériel").bulk() > 0);
    }
}
