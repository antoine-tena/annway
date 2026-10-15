package fr.annway.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class DriverTest {

    @Test
    void driverNeedsAtLeastOneVehicle() {
        var error = assertThrows(AccountException.class,
                () -> new Driver(1, "Alan", "Turing", "alan@example.org", "0611111111", List.of()));
        assertEquals(AccountError.DRIVER_WITHOUT_VEHICLE, error.error());
    }

    @Test
    void addsAnotherVehicle() {
        var driver = AccountFixtures.driver(1);
        driver.addVehicle(AccountFixtures.vehicle("EF-456-GH"));
        assertEquals(List.of("AB-123-CD", "EF-456-GH"),
                driver.vehicles().stream().map(Vehicle::registration).toList());
    }

    @Test
    void rejectsSameRegistrationTwice() {
        var driver = AccountFixtures.driver(1);
        var error = assertThrows(AccountException.class,
                () -> driver.addVehicle(AccountFixtures.vehicle("ab-123-cd")));
        assertEquals(AccountError.VEHICLE_ALREADY_REGISTERED, error.error());
    }

    @Test
    void vehiclesCannotBeModifiedFromOutside() {
        var driver = AccountFixtures.driver(1);
        assertThrows(UnsupportedOperationException.class, () -> driver.vehicles().clear());
    }
}
