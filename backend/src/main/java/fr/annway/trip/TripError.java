package fr.annway.trip;

/** Erreurs métier du domaine des trajets : un code machine et un message pour l'utilisateur. */
public enum TripError {
    BLANK_FIELD("Un champ obligatoire est vide."),
    INVALID_TRIP_ID("L'identifiant du trajet n'est pas valide."),
    SAME_DEPARTURE_AND_ARRIVAL("Le départ et l'arrivée doivent être différents."),
    VEHICLE_NOT_OWNED("Ce véhicule n'appartient pas au conducteur."),
    INVALID_PRICE("Le prix par place doit être positif."),
    INVALID_SEAT_COUNT("Il faut réserver au moins une place."),
    INVALID_PASSENGER_COUNT("Il faut au moins un passager."),
    STOP_BEFORE_DEPARTURE("Une étape ne peut pas précéder le départ."),
    BUS_BEFORE_TRIP_DEPARTURE("Le bus part avant l'heure de départ du trajet.");

    private final String userMessage;

    TripError(String userMessage) {
        this.userMessage = userMessage;
    }

    public String userMessage() {
        return userMessage;
    }
}
