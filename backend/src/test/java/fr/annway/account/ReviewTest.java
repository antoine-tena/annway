package fr.annway.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ReviewTest {

    private final Passenger author = AccountFixtures.passenger(1);
    private final Driver target = AccountFixtures.driver(2);

    private Review leaveReview(int rating) {
        return author.leaveReview(target, rating, " Très bon trajet ", AccountFixtures.LATE_EVENING_CLOCK);
    }

    @Test
    void keepsAuthorTargetRatingAndComment() {
        var review = leaveReview(4);
        assertSame(author, review.author());
        assertSame(target, review.target());
        assertEquals(4, review.rating());
        assertEquals("Très bon trajet", review.comment());
        assertEquals(Optional.empty(), review.reply());
    }

    @Test
    void dateFollowsParisCalendar() {
        assertEquals(LocalDate.of(2027, 1, 15), leaveReview(4).date());
    }

    @ParameterizedTest
    @ValueSource(ints = {Review.MIN_RATING, Review.MAX_RATING})
    void acceptsBoundaryRatings(int rating) {
        assertEquals(rating, leaveReview(rating).rating());
    }

    @ParameterizedTest
    @ValueSource(ints = {Review.MIN_RATING - 1, Review.MAX_RATING + 1})
    void rejectsRatingOutOfRange(int rating) {
        var error = assertThrows(AccountException.class, () -> leaveReview(rating));
        assertEquals(AccountError.INVALID_RATING, error.error());
        assertTrue(target.receivedReviews().isEmpty());
    }

    @Test
    void cannotReviewOneself() {
        var error = assertThrows(AccountException.class,
                () -> author.leaveReview(author, 5, "", AccountFixtures.LATE_EVENING_CLOCK));
        assertEquals(AccountError.SELF_REVIEW, error.error());
    }

    @Test
    void targetRepliesOnce() {
        var review = leaveReview(4);
        review.reply(target, " Merci ! ");
        assertEquals(Optional.of("Merci !"), review.reply());
        var error = assertThrows(AccountException.class, () -> review.reply(target, "Encore merci"));
        assertEquals(AccountError.REVIEW_ALREADY_ANSWERED, error.error());
    }

    @Test
    void onlyTargetCanReply() {
        var review = leaveReview(4);
        var error = assertThrows(AccountException.class, () -> review.reply(author, "Je réponds"));
        assertEquals(AccountError.REPLY_BY_NON_TARGET, error.error());
    }

    @Test
    void blankReplyIsRejected() {
        var review = leaveReview(4);
        var error = assertThrows(AccountException.class, () -> review.reply(target, "   "));
        assertEquals(AccountError.BLANK_FIELD, error.error());
    }
}
