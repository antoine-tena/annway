package fr.annway.equipment;

/** Erreurs métier du domaine du matériel : un code machine et un message pour l'utilisateur. */
public enum EquipmentError {
    BLANK_DESCRIPTION("La description du matériel est vide.");

    private final String userMessage;

    EquipmentError(String userMessage) {
        this.userMessage = userMessage;
    }

    public String userMessage() {
        return userMessage;
    }
}
