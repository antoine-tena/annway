package fr.annway.payment;

import fr.annway.account.User;
import java.lang.System.Logger.Level;
import java.util.Objects;

/**
 * Adaptateur : présente l'API de paiement tierce sous le contrat {@link PaymentService}. Le
 * journal ne garde que l'identifiant du payeur, jamais son e-mail ([DONNEES-PERSO]).
 */
public final class ExternalPaymentAdapter implements PaymentService {

    private static final System.Logger LOG = System.getLogger(ExternalPaymentAdapter.class.getName());

    private final ExternalPaymentApi api;

    public ExternalPaymentAdapter(ExternalPaymentApi api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    @Override
    public boolean pay(User payer, long amountCents) {
        Objects.requireNonNull(payer, "payer");
        var transactionId = api.sendPayment(payer.email(), amountCents);
        var isAccepted = transactionId != null && !transactionId.isBlank();
        LOG.log(Level.INFO, "payment sent userId={0,number,#} amountCents={1,number,#} accepted={2}",
                payer.id(), amountCents, isAccepted);
        return isAccepted;
    }
}
