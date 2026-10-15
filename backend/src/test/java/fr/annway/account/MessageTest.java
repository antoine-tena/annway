package fr.annway.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class MessageTest {

    private final Passenger sender = AccountFixtures.passenger(1);

    @Test
    void sendsTrimmedMessageAtClockInstant() {
        var message = sender.sendMessage(12, "  Il reste une place ?  ", AccountFixtures.LATE_EVENING_CLOCK);
        assertSame(sender, message.sender());
        assertEquals(12, message.tripId());
        assertEquals("Il reste une place ?", message.content());
        assertEquals(Instant.parse("2027-01-14T23:30:00Z"), message.sentAt());
    }

    @Test
    void rejectsUnknownTrip() {
        var error = assertThrows(AccountException.class,
                () -> sender.sendMessage(0, "Bonjour", AccountFixtures.LATE_EVENING_CLOCK));
        assertEquals(AccountError.INVALID_TRIP_ID, error.error());
    }

    @Test
    void rejectsBlankContent() {
        var error = assertThrows(AccountException.class,
                () -> sender.sendMessage(12, "   ", AccountFixtures.LATE_EVENING_CLOCK));
        assertEquals(AccountError.BLANK_FIELD, error.error());
    }

    @Test
    void acceptsMaximumLength() {
        var longestContent = "a".repeat(Message.MAX_CONTENT_LENGTH);
        var message = sender.sendMessage(12, longestContent, AccountFixtures.LATE_EVENING_CLOCK);
        assertEquals(Message.MAX_CONTENT_LENGTH, message.content().length());
    }

    @Test
    void rejectsTooLongContent() {
        var tooLongContent = "a".repeat(Message.MAX_CONTENT_LENGTH + 1);
        var error = assertThrows(AccountException.class,
                () -> sender.sendMessage(12, tooLongContent, AccountFixtures.LATE_EVENING_CLOCK));
        assertEquals(AccountError.MESSAGE_TOO_LONG, error.error());
    }

    @Test
    void toStringHidesContent() {
        var message = sender.sendMessage(12, "Mon numéro : 0600000000", AccountFixtures.LATE_EVENING_CLOCK);
        assertEquals("Message[senderId=1, tripId=12]", message.toString());
        assertFalse(message.toString().contains("0600000000"));
    }
}
