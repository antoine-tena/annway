package fr.annway.equipment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class EquipmentFactoryTest {

    @ParameterizedTest
    @EnumSource(EquipmentType.class)
    void createsEquipmentOfRequestedType(EquipmentType type) {
        assertEquals(type, EquipmentFactory.create(type, "Mon matériel").type());
    }

    @Test
    void createsMatchingSubclasses() {
        assertInstanceOf(Ski.class, EquipmentFactory.create(EquipmentType.SKI, "Rossignol 170"));
        assertInstanceOf(Snowboard.class, EquipmentFactory.create(EquipmentType.SNOWBOARD, "Burton"));
        assertInstanceOf(Sled.class, EquipmentFactory.create(EquipmentType.SLED, "Luge en bois"));
    }

    @Test
    void stripsDescription() {
        assertEquals("Rossignol 170", EquipmentFactory.create(EquipmentType.SKI, "  Rossignol 170 ").description());
    }

    @Test
    void rejectsBlankDescription() {
        var error = assertThrows(EquipmentException.class, () -> EquipmentFactory.create(EquipmentType.SLED, "  "));
        assertEquals(EquipmentError.BLANK_DESCRIPTION, error.error());
        assertEquals("BLANK_DESCRIPTION", error.code());
    }

    @Test
    void rejectsMissingDescription() {
        assertThrows(NullPointerException.class, () -> EquipmentFactory.create(EquipmentType.SKI, null));
    }

    @Test
    void rejectsMissingType() {
        assertThrows(NullPointerException.class, () -> EquipmentFactory.create(null, "Burton"));
    }
}
