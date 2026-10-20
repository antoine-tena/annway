package fr.annway.web;

import fr.annway.payment.ExternalPaymentApi;
import java.util.UUID;

/**
 * Prestataire de paiement de démonstration : accepte tout paiement et tout remboursement et rend
 * un numéro de transaction, sans rien débiter ni créditer.
 */
public final class DemoPaymentApi implements ExternalPaymentApi {

    @Override
    public String sendPayment(String email, long amountCents) {
        return "demo-" + UUID.randomUUID();
    }

    @Override
    public String sendRefund(String email, long amountCents) {
        return "demo-refund-" + UUID.randomUUID();
    }
}
