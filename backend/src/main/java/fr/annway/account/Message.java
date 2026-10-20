package fr.annway.account;

import fr.annway.trip.Trip;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

/**
 * Message envoyé à propos de l'annonce d'un trajet. L'instant d'envoi est en UTC ([TIME-ZONE]).
 * Créé par {@link User#sendMessage}.
 */
public record Message(User sender, Trip trip, String content, Instant sentAt) {

    static final int MAX_CONTENT_LENGTH = 1000;

    public Message {
        Objects.requireNonNull(sender, "sender");
        Objects.requireNonNull(trip, "trip");
        Objects.requireNonNull(sentAt, "sentAt");
        content = AccountException.requireNotBlank(content, "content");
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new AccountException(AccountError.MESSAGE_TOO_LONG);
        }
    }

    static Message send(User sender, Trip trip, String content, Clock clock) {
        return new Message(sender, trip, content, Objects.requireNonNull(clock, "clock").instant());
    }

    /** N'expose ni l'expéditeur ni le contenu, qui peut contenir des données personnelles. */
    @Override
    public String toString() {
        return "Message[senderId=" + sender.id() + ", tripId=" + trip.id() + "]";
    }
}
