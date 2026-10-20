package fr.annway.trip;

/**
 * Cycle de vie d'une réservation : en attente de la réponse du conducteur, puis close (refusée,
 * annulée par le passager, ou close avec son trajet annulé).
 */
public enum ReservationStatus {
    PENDING,
    VALIDATED,
    REFUSED,
    CANCELLED,
    TRIP_CANCELLED
}
