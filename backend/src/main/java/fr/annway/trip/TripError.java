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
    BUS_BEFORE_TRIP_DEPARTURE("Le bus part avant l'heure de départ du trajet."),
    NO_ROOM("Il n'y a plus assez de places ou de place pour le matériel."),
    TRIP_NOT_DEPARTED("Le trajet n'est pas encore parti."),
    TRIP_CANCELLED("Ce trajet est annulé."),
    TRIP_FINISHED("Ce trajet est terminé."),
    INVALID_RESERVATION_ID("L'identifiant de la réservation n'est pas valide."),
    NOT_TRIP_DRIVER("Seul le conducteur du trajet peut faire cela."),
    NOT_RESERVATION_PASSENGER("Seul le passager de la réservation peut faire cela."),
    RESERVATION_NOT_PENDING("Cette réservation n'attend plus de réponse."),
    RESERVATION_CLOSED("Cette réservation est refusée ou annulée."),
    PAYMENT_REQUIRED("Ce trajet doit être payé avant d'être validé."),
    ALREADY_PAID("Cette réservation est déjà payée."),
    WRONG_AMOUNT("Le montant ne correspond pas au prix du trajet."),
    INVALID_AMOUNT("Le montant doit être positif."),
    PAYMENT_DECLINED("Le paiement a été refusé.");

    private final String userMessage;

    TripError(String userMessage) {
        this.userMessage = userMessage;
    }

    public String userMessage() {
        return userMessage;
    }
}
