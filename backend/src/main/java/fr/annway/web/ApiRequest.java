package fr.annway.web;

import java.util.Objects;
import java.util.Optional;

/**
 * Requête reçue par l'API, détachée du serveur HTTP : méthode, chemin, paramètres d'URL, corps
 * de formulaire, jeton de session lu dans le cookie et clé du client pour la limite de débit
 * (adresse tronquée, jamais l'adresse complète : [PERSONAL-DATA]).
 */
public record ApiRequest(String method, String path, FormData query, FormData form,
        Optional<String> sessionToken, String clientKey) {

    public ApiRequest {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(form, "form");
        Objects.requireNonNull(sessionToken, "sessionToken");
        Objects.requireNonNull(clientKey, "clientKey");
    }
}
