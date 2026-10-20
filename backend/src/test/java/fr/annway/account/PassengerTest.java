package fr.annway.account;

import static fr.annway.account.AccountFixtures.LATE_EVENING_CLOCK;
import static fr.annway.account.AccountFixtures.TO_CHAMROUSSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.annway.equipment.EquipmentFactory;
import fr.annway.equipment.EquipmentType;
import fr.annway.search.TripCatalogue;
import fr.annway.trip.ReservationStatus;
import fr.annway.trip.TripError;
import fr.annway.trip.TripException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class PassengerTest {

    private final Passenger passenger = AccountFixtures.passenger(1);
    private final Driver driver = AccountFixtures.driver(2);
    private final TripCatalogue catalogue = new TripCatalogue();

    @Test
    void searchFindsPublishedTripsMatchingCriteria() {
        var trip = AccountFixtures.stationTrip(1, driver);
        driver.publishTrip(trip, catalogue);
        assertEquals(List.of(trip), passenger.searchTrip(TO_CHAMROUSSE, catalogue));
    }

    @Test
    void bookingIsPendingWithDeclaredEquipment() {
        var trip = AccountFixtures.stationTrip(1, driver);
        var skis = EquipmentFactory.create(EquipmentType.SKI, "Skis de randonnée");
        var reservation = passenger.book(5, trip, 2, List.of(skis), LATE_EVENING_CLOCK);
        assertSame(passenger, reservation.passenger());
        assertSame(trip, reservation.trip());
        assertEquals(2, reservation.seatCount());
        assertEquals(List.of(skis), reservation.equipment());
        assertEquals(ReservationStatus.PENDING, reservation.status());
    }

    @Test
    void bookingMoreSeatsThanTheVehicleHasIsRefused() {
        var trip = AccountFixtures.stationTrip(1, driver);
        var error = assertThrows(TripException.class,
                () -> passenger.book(5, trip, trip.availableSeats() + 1, List.of(), LATE_EVENING_CLOCK));
        assertEquals(TripError.NO_ROOM, error.error());
    }

    @Test
    void tripRequestIsDatedInParisCalendar() {
        var request = passenger.createTripRequest(TO_CHAMROUSSE, LATE_EVENING_CLOCK);
        assertSame(passenger, request.passenger());
        assertEquals(TO_CHAMROUSSE, request.criteria());
        assertEquals(LocalDate.of(2027, 1, 15), request.createdOn());
    }

    @Test
    void subscribedAlertKeepsTripsPublishedAfterward() {
        var alert = passenger.subscribeAlert(TO_CHAMROUSSE, catalogue);
        var trip = AccountFixtures.stationTrip(1, driver);
        driver.publishTrip(trip, catalogue);
        assertSame(passenger, alert.passenger());
        assertEquals(List.of(trip), alert.matchedTrips());
    }

    @Test
    void rejectsMissingCatalogue() {
        assertThrows(NullPointerException.class, () -> passenger.searchTrip(TO_CHAMROUSSE, null));
        assertThrows(NullPointerException.class, () -> passenger.subscribeAlert(TO_CHAMROUSSE, null));
    }
}
