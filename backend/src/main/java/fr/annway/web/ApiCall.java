package fr.annway.web;

import fr.annway.account.Driver;
import fr.annway.account.Passenger;
import fr.annway.account.User;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Appel d'une route : la requête, les paramètres du chemin et l'utilisateur de la session. */
public record ApiCall(ApiRequest request, Map<String, String> pathParams, Optional<User> user) {

    public ApiCall {
        Objects.requireNonNull(request, "request");
        pathParams = Map.copyOf(pathParams);
        Objects.requireNonNull(user, "user");
    }

    public FormData form() {
        return request.form();
    }

    public FormData query() {
        return request.query();
    }

    public User member() {
        return user.orElseThrow(() -> new WebException(WebError.UNAUTHENTICATED));
    }

    public Passenger passenger() {
        if (member() instanceof Passenger passenger) {
            return passenger;
        }
        throw new WebException(WebError.FORBIDDEN);
    }

    public Driver driver() {
        if (member() instanceof Driver driver) {
            return driver;
        }
        throw new WebException(WebError.FORBIDDEN);
    }

    /** Identifiant numérique du chemin ({@code /api/trips/{id}}) ; mal formé, la ressource n'existe pas. */
    public int pathId(String name) {
        var text = pathParams.getOrDefault(name, "");
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException malformed) {
            throw new WebException(WebError.NOT_FOUND);
        }
    }
}
