package fr.annway.account;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

/**
 * Avis laissé par un utilisateur sur un autre : une note de 1 à 5, un commentaire et, une seule
 * fois, la réponse de la personne évaluée. Créé par {@link User#leaveReview}.
 */
public final class Review {

    static final int MIN_RATING = 1;
    static final int MAX_RATING = 5;
    static final ZoneId CALENDAR_ZONE = ZoneId.of("Europe/Paris");

    private final User author;
    private final User target;
    private final int rating;
    private final String comment;
    private final LocalDate date;
    private String reply = "";

    Review(User author, User target, int rating, String comment, Clock clock) {
        this.author = Objects.requireNonNull(author, "author");
        this.target = Objects.requireNonNull(target, "target");
        if (author == target) {
            throw new AccountException(AccountError.SELF_REVIEW);
        }
        if (rating < MIN_RATING || rating > MAX_RATING) {
            throw new AccountException(AccountError.INVALID_RATING);
        }
        this.rating = rating;
        this.comment = Objects.requireNonNull(comment, "comment").strip();
        this.date = LocalDate.ofInstant(Objects.requireNonNull(clock, "clock").instant(), CALENDAR_ZONE);
    }

    /** Répond à l'avis ; seule la personne évaluée le peut, et une seule fois. */
    public void reply(User responder, String replyText) {
        Objects.requireNonNull(responder, "responder");
        if (responder != target) {
            throw new AccountException(AccountError.REPLY_BY_NON_TARGET);
        }
        if (!reply.isEmpty()) {
            throw new AccountException(AccountError.REVIEW_ALREADY_ANSWERED);
        }
        reply = AccountException.requireNotBlank(replyText, "replyText");
    }

    public User author() {
        return author;
    }

    public User target() {
        return target;
    }

    public int rating() {
        return rating;
    }

    public String comment() {
        return comment;
    }

    public LocalDate date() {
        return date;
    }

    public Optional<String> reply() {
        return reply.isEmpty() ? Optional.empty() : Optional.of(reply);
    }
}
