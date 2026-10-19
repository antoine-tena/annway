package fr.annway.trip;

/** Cycle de vie d'une réservation : en attente de la réponse du conducteur, puis close. */
public enum ReservationStatus {
    PENDING,
    VALIDATED,
    REFUSED,
    CANCELLED
}
