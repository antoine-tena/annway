package fr.annway.web;

import fr.annway.account.Driver;
import fr.annway.account.Review;
import fr.annway.account.User;
import fr.annway.account.Vehicle;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Comptes en JSON. Le résumé public ne montre que le prénom, la note et les vérifications ;
 * nom, e-mail et téléphone ne sortent que vers leur propriétaire ([PERSONAL-DATA]).
 */
public final class AccountViews {

    private static final double RATING_ROUNDING = 10.0;

    private AccountViews() {
    }

    public static JsonObject summary(User user) {
        var average = user.averageRating();
        Optional<Double> rounded = average.isPresent()
                ? Optional.of(Math.round(average.getAsDouble() * RATING_ROUNDING) / RATING_ROUNDING)
                : Optional.empty();
        return new JsonObject()
                .put("id", user.id())
                .put("firstName", user.firstName())
                .put("role", Role.of(user))
                .put("averageRating", rounded)
                .put("reviewCount", user.receivedReviews().size())
                .put("isVerified", user.profile().isVerified())
                .put("bio", user.profile().bio());
    }

    /** Profil public d'un membre : son résumé, ses véhicules et les avis reçus. */
    public static JsonObject publicProfile(User user) {
        return summary(user)
                .put("vehicles", vehiclesOf(user))
                .put("reviews", reviews(user));
    }

    /** Compte de l'utilisateur connecté, avec ses coordonnées. */
    public static JsonObject me(User user) {
        var profile = user.profile();
        var verifications = new JsonObject()
                .put("email", profile.isEmailVerified())
                .put("phone", profile.isPhoneVerified())
                .put("identityDocument", profile.isIdentityDocumentVerified());
        return publicProfile(user)
                .put("lastName", user.lastName())
                .put("email", user.email())
                .put("phone", user.phone())
                .put("photoUrl", profile.photoUrl())
                .put("verifications", verifications);
    }

    public static JsonObject vehicle(Vehicle vehicle) {
        return new JsonObject()
                .put("registration", vehicle.registration())
                .put("model", vehicle.model())
                .put("seatCount", vehicle.seatCount())
                .put("equipmentCapacity", vehicle.equipmentCapacity());
    }

    private static List<JsonObject> vehiclesOf(User user) {
        if (user instanceof Driver driver) {
            return driver.vehicles().stream().map(AccountViews::vehicle).toList();
        }
        return List.of();
    }

    /** Avis reçus, numérotés dans leur ordre d'arrivée : le numéro sert à y répondre. */
    private static List<JsonObject> reviews(User user) {
        var received = user.receivedReviews();
        return IntStream.range(0, received.size())
                .mapToObj(index -> review(received.get(index), index))
                .toList();
    }

    private static JsonObject review(Review review, int index) {
        return new JsonObject()
                .put("index", index)
                .put("author", new JsonObject()
                        .put("id", review.author().id())
                        .put("firstName", review.author().firstName()))
                .put("rating", review.rating())
                .put("comment", review.comment())
                .put("date", review.date().toString())
                .put("reply", review.reply());
    }
}
