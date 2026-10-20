package fr.annway.web;

import fr.annway.account.Driver;
import fr.annway.account.Passenger;
import fr.annway.account.User;

/** Rôle d'un compte, tel que l'API l'écrit et que l'inscription le reçoit. */
public enum Role {
    PASSENGER,
    DRIVER;

    public static Role of(User user) {
        return switch (user) {
            case Passenger ignored -> PASSENGER;
            case Driver ignored -> DRIVER;
        };
    }
}
