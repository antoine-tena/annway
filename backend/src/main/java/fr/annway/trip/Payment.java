package fr.annway.trip;

import java.time.Instant;
import java.util.Objects;

/** Paiement accepté d'une réservation : montant en centimes ([MONEY]), instant UTC ([TIME-ZONE]). */
public record Payment(long amountCents, PaymentType type, Instant paidAt) {

    public Payment {
        if (amountCents < 1) {
            throw new TripException(TripError.INVALID_AMOUNT);
        }
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(paidAt, "paidAt");
    }
}
