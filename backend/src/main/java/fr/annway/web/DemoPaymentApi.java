package fr.annway.web;

import fr.annway.payment.ExternalPaymentApi;
import java.util.UUID;

/**
 * Prestataire de paiement de démonstration : accepte tout paiement et rend un numéro de
 * transaction, sans rien débiter, le temps qu'un vrai prestataire soit choisi.
 */
public final class DemoPaymentApi implements ExternalPaymentApi {

    @Override
    public String sendPayment(String email, long amountCents) {
        return "demo-" + UUID.randomUUID();
    }
}
