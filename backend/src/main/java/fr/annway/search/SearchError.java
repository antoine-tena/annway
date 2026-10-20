package fr.annway.search;

/** Erreurs métier de la recherche de trajets : un code machine et un message pour l'utilisateur. */
public enum SearchError {
    NEGATIVE_MAX_PRICE("Le prix maximum ne peut pas être négatif."),
    INVERTED_TIME_WINDOW("L'heure de début de la plage horaire est après l'heure de fin.");

    private final String userMessage;

    SearchError(String userMessage) {
        this.userMessage = userMessage;
    }

    public String userMessage() {
        return userMessage;
    }
}
