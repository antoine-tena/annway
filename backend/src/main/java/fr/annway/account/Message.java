package fr.annway.account;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

/**
 * Message envoyé à propos d'une annonce de trajet. L'annonce est désignée par l'identifiant du
 * trajet, pour ne pas dépendre de la classe des trajets ([PROPRIETAIRE-CLASSE]). L'instant
 * d'envoi est en UTC ([TIME-ZONE]). Créé par {@link User#sendMessage}.
 */
public record Message(User sender, int tripId, String content, Instant sentAt) {

    static final int MAX_CONTENT_LENGTH = 1000;

    public Message {
        Objects.requireNonNull(sender, "sender");
        Objects.requireNonNull(sentAt, "sentAt");
        if (tripId < 1) {
            throw new AccountException(AccountError.INVALID_TRIP_ID);
        }
        content = AccountException.requireNotBlank(content, "content");
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new AccountException(AccountError.MESSAGE_TOO_LONG);
        }
    }

    static Message send(User sender, int tripId, String content, Clock clock) {
        return new Message(sender, tripId, content, Objects.requireNonNull(clock, "clock").instant());
    }

    /** N'expose ni l'expéditeur ni le contenu, qui peut contenir des données personnelles. */
    @Override
    public String toString() {
        return "Message[senderId=" + sender.id() + ", tripId=" + tripId + "]";
    }
}
