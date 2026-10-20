package fr.annway.web;

import fr.annway.account.Driver;
import fr.annway.account.Passenger;
import fr.annway.account.User;
import java.util.Optional;

/**
 * Authentification que chaque route déclare ([PERMISSIONS]). Une route publique dit pourquoi
 * elle l'est, dans sa table de routes, et passe par la limite de débit.
 */
public enum Access {
    PUBLIC,
    MEMBER,
    PASSENGER,
    DRIVER;

    /** 401 sans session, 403 quand le rôle ne convient pas. */
    void check(Optional<User> user) {
        if (this == PUBLIC) {
            return;
        }
        var member = user.orElseThrow(() -> new WebException(WebError.UNAUTHENTICATED));
        var isAllowed = switch (this) {
            case PASSENGER -> member instanceof Passenger;
            case DRIVER -> member instanceof Driver;
            case PUBLIC, MEMBER -> true;
        };
        if (!isAllowed) {
            throw new WebException(WebError.FORBIDDEN);
        }
    }
}
