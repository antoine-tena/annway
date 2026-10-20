package fr.annway.trip;

import java.time.Instant;
import java.util.Objects;

/** Remboursement accepté d'une réservation : montant en centimes ([MONEY]), instant UTC ([TIME-ZONE]). */
public record Refund(long amountCents, Instant refundedAt) {

    public Refund {
        if (amountCents < 1) {
            throw new TripException(TripError.INVALID_AMOUNT);
        }
        Objects.requireNonNull(refundedAt, "refundedAt");
    }
}
