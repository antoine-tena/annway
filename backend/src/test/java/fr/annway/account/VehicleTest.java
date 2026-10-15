package fr.annway.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class VehicleTest {

    @Test
    void normalizesRegistrationAndModel() {
        var vehicle = new Vehicle(" ab-123-cd ", " Clio ", 4, 0);
        assertEquals("AB-123-CD", vehicle.registration());
        assertEquals("Clio", vehicle.model());
    }

    @Test
    void rejectsVehicleWithoutSeat() {
        var error = assertThrows(AccountException.class, () -> new Vehicle("AB-123-CD", "Clio", 0, 2));
        assertEquals(AccountError.INVALID_SEAT_COUNT, error.error());
    }

    @Test
    void rejectsNegativeEquipmentCapacity() {
        var error = assertThrows(AccountException.class, () -> new Vehicle("AB-123-CD", "Clio", 4, -1));
        assertEquals("INVALID_EQUIPMENT_CAPACITY", error.code());
    }

    @Test
    void rejectsBlankRegistration() {
        var error = assertThrows(AccountException.class, () -> new Vehicle("  ", "Clio", 4, 2));
        assertEquals(AccountError.BLANK_FIELD, error.error());
    }

    @Test
    void rejectsMissingModel() {
        assertThrows(NullPointerException.class, () -> new Vehicle("AB-123-CD", null, 4, 2));
    }
}
