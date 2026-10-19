package fr.annway.trip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.annway.account.Driver;
import fr.annway.account.Vehicle;
import fr.annway.equipment.EquipmentFactory;
import fr.annway.equipment.EquipmentType;
import java.util.List;
import org.junit.jupiter.api.Test;

class TripTest {

    @Test
    void startsOpenWithTheVehicleCapacity() {
        var trip = TripFixtures.stationTrip();
        assertEquals(TripStatus.OPEN, trip.status());
        assertEquals(3, trip.availableSeats());
        assertEquals(4, trip.remainingEquipmentCapacity());
    }

    @Test
    void rejectsVehicleOfAnotherDriver() {
        var stranger = new Vehicle("ZZ-999-ZZ", "Kangoo", 4, 4);
        Driver driver = TripFixtures.driver();
        var error = assertThrows(TripException.class,
                () -> new StationTrip(TripFixtures.route(), driver, stranger, 1000));
        assertEquals(TripError.VEHICLE_NOT_OWNED, error.error());
    }

    @Test
    void addsStopsAfterDeparture() {
        var trip = TripFixtures.stationTrip();
        var stop = new Stop("Uriage", TripFixtures.LATE_EVENING.plusSeconds(600));
        trip.addStop(stop);
        assertEquals(List.of(stop), trip.stops());
    }

    @Test
    void rejectsStopBeforeDeparture() {
        var trip = TripFixtures.stationTrip();
        var stop = new Stop("Uriage", TripFixtures.LATE_EVENING.minusSeconds(60));
        var error = assertThrows(TripException.class, () -> trip.addStop(stop));
        assertEquals(TripError.STOP_BEFORE_DEPARTURE, error.error());
    }

    @Test
    void hasRoomForSeatsAndEquipmentWithinCapacity() {
        var trip = TripFixtures.stationTrip();
        var snowboard = EquipmentFactory.create(EquipmentType.SNOWBOARD, "Planche");
        assertTrue(trip.hasRoomFor(3, List.of()));
        assertFalse(trip.hasRoomFor(4, List.of()));
        assertTrue(trip.hasRoomFor(1, List.of(snowboard)));
        assertFalse(trip.hasRoomFor(1, List.of(snowboard, snowboard, snowboard, snowboard, snowboard)));
    }

    @Test
    void cancelledTripHasNoRoom() {
        var trip = TripFixtures.stationTrip();
        trip.cancel();
        assertEquals(TripStatus.CANCELLED, trip.status());
        assertFalse(trip.hasRoomFor(1, List.of()));
    }

    @Test
    void hasRoomForRejectsZeroSeat() {
        var trip = TripFixtures.stationTrip();
        var error = assertThrows(TripException.class, () -> trip.hasRoomFor(0, List.of()));
        assertEquals(TripError.INVALID_SEAT_COUNT, error.error());
    }
}
