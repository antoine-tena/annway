package fr.annway.trip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TripValuesTest {

    private static final LocalDate DAY = LocalDate.of(2027, 1, 15);

    @Test
    void routeRejectsInvalidId() {
        var error = assertThrows(TripException.class,
                () -> new TripRoute(0, "Grenoble", "Chamrousse", TripFixtures.LATE_EVENING));
        assertEquals(TripError.INVALID_TRIP_ID, error.error());
    }

    @Test
    void routeRejectsSameDepartureAndArrival() {
        var error = assertThrows(TripException.class,
                () -> new TripRoute(1, "Grenoble", " grenoble ", TripFixtures.LATE_EVENING));
        assertEquals(TripError.SAME_DEPARTURE_AND_ARRIVAL, error.error());
    }

    @Test
    void routeRejectsBlankPlace() {
        var error = assertThrows(TripException.class,
                () -> new TripRoute(1, " ", "Chamrousse", TripFixtures.LATE_EVENING));
        assertEquals("BLANK_FIELD", error.code());
    }

    @Test
    void stopIsStripped() {
        assertEquals("Vizille", new Stop(" Vizille ", Instant.EPOCH).place());
    }

    @Test
    void criteriaNeedAtLeastOnePassenger() {
        var error = assertThrows(TripException.class,
                () -> new SearchCriteria("Grenoble", "Chamrousse", DAY, 0));
        assertEquals(TripError.INVALID_PASSENGER_COUNT, error.error());
    }

    @Test
    void criteriaWithoutTypeAcceptAnyType() {
        var criteria = new SearchCriteria("Grenoble", "Chamrousse", DAY, 1);
        assertEquals(Optional.empty(), criteria.type());
    }
}
