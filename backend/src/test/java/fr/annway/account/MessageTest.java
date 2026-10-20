package fr.annway.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.annway.trip.Trip;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class MessageTest {

    private final Passenger sender = AccountFixtures.passenger(1);
    private final Trip trip = AccountFixtures.stationTrip(12, AccountFixtures.driver(2));

    @Test
    void sendsTrimmedMessageAtClockInstant() {
        var message = sender.sendMessage(trip, "  Il reste une place ?  ", AccountFixtures.LATE_EVENING_CLOCK);
        assertSame(sender, message.sender());
        assertSame(trip, message.trip());
        assertEquals("Il reste une place ?", message.content());
        assertEquals(Instant.parse("2027-01-14T23:30:00Z"), message.sentAt());
    }

    @Test
    void rejectsMissingTrip() {
        assertThrows(NullPointerException.class,
                () -> sender.sendMessage(null, "Bonjour", AccountFixtures.LATE_EVENING_CLOCK));
    }

    @Test
    void rejectsBlankContent() {
        var error = assertThrows(AccountException.class,
                () -> sender.sendMessage(trip, "   ", AccountFixtures.LATE_EVENING_CLOCK));
        assertEquals(AccountError.BLANK_FIELD, error.error());
    }

    @Test
    void acceptsMaximumLength() {
        var longestContent = "a".repeat(Message.MAX_CONTENT_LENGTH);
        var message = sender.sendMessage(trip, longestContent, AccountFixtures.LATE_EVENING_CLOCK);
        assertEquals(Message.MAX_CONTENT_LENGTH, message.content().length());
    }

    @Test
    void rejectsTooLongContent() {
        var tooLongContent = "a".repeat(Message.MAX_CONTENT_LENGTH + 1);
        var error = assertThrows(AccountException.class,
                () -> sender.sendMessage(trip, tooLongContent, AccountFixtures.LATE_EVENING_CLOCK));
        assertEquals(AccountError.MESSAGE_TOO_LONG, error.error());
    }

    @Test
    void toStringHidesContent() {
        var message = sender.sendMessage(trip, "Mon numéro : 0600000000", AccountFixtures.LATE_EVENING_CLOCK);
        assertEquals("Message[senderId=1, tripId=12]", message.toString());
        assertFalse(message.toString().contains("0600000000"));
    }
}
