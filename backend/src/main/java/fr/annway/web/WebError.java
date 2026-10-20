package fr.annway.web;

/** Erreurs de l'API web : un statut HTTP et un message pour l'utilisateur ([DOMAIN-ERRORS]). */
public enum WebError {
    INVALID_FIELD(HttpStatus.BAD_REQUEST, "Un champ est manquant ou invalide."),
    WEAK_PASSWORD(HttpStatus.BAD_REQUEST, "Le mot de passe doit faire au moins 10 caractères."),
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Connectez-vous pour continuer."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "E-mail ou mot de passe incorrect."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Vous n'avez pas accès à cette action."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Cette ressource n'existe pas."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Cette méthode n'est pas acceptée ici."),
    EMAIL_TAKEN(HttpStatus.CONFLICT, "Un compte existe déjà avec cette adresse e-mail."),
    BODY_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "La requête est trop volumineuse."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "Trop de requêtes : réessayez dans une minute."),
    INTERNAL(HttpStatus.INTERNAL_ERROR, "Une erreur inattendue est survenue.");

    private final int status;
    private final String userMessage;

    WebError(int status, String userMessage) {
        this.status = status;
        this.userMessage = userMessage;
    }

    public int status() {
        return status;
    }

    public String userMessage() {
        return userMessage;
    }
}
