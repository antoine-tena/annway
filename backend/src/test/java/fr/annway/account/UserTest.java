package fr.annway.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.OptionalDouble;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void keepsTrimmedIdentity() {
        var passenger = new Passenger(7, " Ada ", " Lovelace ", " ada@example.org ", " 0600000000 ");
        assertEquals(7, passenger.id());
        assertEquals("Ada", passenger.firstName());
        assertEquals("Lovelace", passenger.lastName());
        assertEquals("ada@example.org", passenger.email());
        assertEquals("0600000000", passenger.phone());
    }

    @Test
    void rejectsInvalidEmail() {
        var error = assertThrows(AccountException.class,
                () -> new Passenger(1, "Ada", "Lovelace", "ada.example.org", "0600000000"));
        assertEquals(AccountError.INVALID_EMAIL, error.error());
    }

    @Test
    void rejectsBlankPhone() {
        var error = assertThrows(AccountException.class,
                () -> new Passenger(1, "Ada", "Lovelace", "ada@example.org", " "));
        assertEquals(AccountError.BLANK_FIELD, error.error());
    }

    @Test
    void toStringHidesPersonalData() {
        var description = AccountFixtures.passenger(42).toString();
        assertEquals("Passenger[id=42]", description);
        assertFalse(description.contains("ada@example.org"));
    }

    @Test
    void eachUserOwnsOneProfile() {
        var passenger = AccountFixtures.passenger(1);
        passenger.profile().verifyEmail();
        assertSame(passenger.profile(), passenger.profile());
        assertTrue(passenger.profile().isEmailVerified());
    }

    @Test
    void averageRatingIsEmptyWithoutReview() {
        assertEquals(OptionalDouble.empty(), AccountFixtures.driver(1).averageRating());
    }

    @Test
    void averageRatingOfReceivedReviews() {
        var driver = AccountFixtures.driver(1);
        AccountFixtures.passenger(2).leaveReview(driver, 5, "Parfait", AccountFixtures.LATE_EVENING_CLOCK);
        AccountFixtures.passenger(3).leaveReview(driver, 2, "En retard", AccountFixtures.LATE_EVENING_CLOCK);
        assertEquals(OptionalDouble.of(3.5), driver.averageRating());
        assertEquals(2, driver.receivedReviews().size());
    }

    @Test
    void receivedReviewsCannotBeModifiedFromOutside() {
        var driver = AccountFixtures.driver(1);
        assertThrows(UnsupportedOperationException.class, () -> driver.receivedReviews().clear());
    }
}
