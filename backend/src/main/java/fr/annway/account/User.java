package fr.annway.account;

import fr.annway.trip.Trip;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.regex.Pattern;

/**
 * Utilisateur d'annway, passager ou conducteur. Il possède son profil et les avis qu'il a reçus.
 * Nom, e-mail et téléphone sont des données personnelles : {@link #toString()} n'expose que
 * l'identifiant ([PERSONAL-DATA]).
 */
public abstract sealed class User permits Passenger, Driver {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    private final int id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final Profile profile = new Profile();
    private final List<Review> receivedReviews = new ArrayList<>();

    protected User(int id, String firstName, String lastName, String email, String phone) {
        this.id = id;
        this.firstName = AccountException.requireNotBlank(firstName, "firstName");
        this.lastName = AccountException.requireNotBlank(lastName, "lastName");
        this.email = AccountException.requireNotBlank(email, "email");
        this.phone = AccountException.requireNotBlank(phone, "phone");
        if (!EMAIL_PATTERN.matcher(this.email).matches()) {
            throw new AccountException(AccountError.INVALID_EMAIL);
        }
    }

    public int id() {
        return id;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String email() {
        return email;
    }

    public String phone() {
        return phone;
    }

    /** Profil modifiable de l'utilisateur (présentation, vérifications). */
    public Profile profile() {
        return profile;
    }

    /** Laisse un avis daté du jour (calendrier de Paris) à un autre utilisateur. */
    public Review leaveReview(User target, int rating, String comment, Clock clock) {
        var review = new Review(this, target, rating, comment, clock);
        target.receivedReviews.add(review);
        return review;
    }

    /** Envoie un message sur l'annonce de ce trajet. */
    public Message sendMessage(Trip trip, String content, Clock clock) {
        return Message.send(this, trip, content, clock);
    }

    public List<Review> receivedReviews() {
        return List.copyOf(receivedReviews);
    }

    /** Moyenne des notes reçues ; vide tant que personne n'a laissé d'avis. */
    public OptionalDouble averageRating() {
        return receivedReviews.stream().mapToInt(Review::rating).average();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + "]";
    }
}
