package fr.annway.account;

/** Erreurs métier du domaine des comptes : un code machine et un message pour l'utilisateur. */
public enum AccountError {
    BLANK_FIELD("Un champ obligatoire est vide."),
    INVALID_EMAIL("L'adresse e-mail n'est pas valide."),
    INVALID_SEAT_COUNT("Un véhicule doit avoir au moins une place assise."),
    INVALID_EQUIPMENT_CAPACITY("La capacité de matériel ne peut pas être négative."),
    DRIVER_WITHOUT_VEHICLE("Un conducteur doit posséder au moins un véhicule."),
    VEHICLE_ALREADY_REGISTERED("Ce véhicule est déjà enregistré."),
    INVALID_RATING("La note doit être comprise entre 1 et 5."),
    SELF_REVIEW("On ne peut pas laisser un avis sur soi-même."),
    REVIEW_ALREADY_ANSWERED("Cet avis a déjà reçu une réponse."),
    REPLY_BY_NON_TARGET("Seule la personne évaluée peut répondre à cet avis."),
    INVALID_TRIP_ID("L'annonce visée n'existe pas."),
    MESSAGE_TOO_LONG("Le message dépasse 1000 caractères.");

    private final String userMessage;

    AccountError(String userMessage) {
        this.userMessage = userMessage;
    }

    public String userMessage() {
        return userMessage;
    }
}
