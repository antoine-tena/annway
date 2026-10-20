package fr.annway.payment;

import fr.annway.account.User;
import java.lang.System.Logger.Level;
import java.util.Objects;

/**
 * Adaptateur : présente l'API de paiement tierce sous les contrats {@link PaymentService} et
 * {@link RefundService}. Le journal ne garde que l'identifiant de l'utilisateur, jamais son
 * e-mail ([PERSONAL-DATA]).
 */
public final class ExternalPaymentAdapter implements PaymentService, RefundService {

    private static final System.Logger LOG = System.getLogger(ExternalPaymentAdapter.class.getName());

    private final ExternalPaymentApi api;

    public ExternalPaymentAdapter(ExternalPaymentApi api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    @Override
    public boolean pay(User payer, long amountCents) {
        Objects.requireNonNull(payer, "payer");
        var isAccepted = isTransaction(api.sendPayment(payer.email(), amountCents));
        LOG.log(Level.INFO, "payment sent userId={0,number,#} amountCents={1,number,#} accepted={2}",
                payer.id(), amountCents, isAccepted);
        return isAccepted;
    }

    @Override
    public boolean refund(User payee, long amountCents) {
        Objects.requireNonNull(payee, "payee");
        var isAccepted = isTransaction(api.sendRefund(payee.email(), amountCents));
        LOG.log(Level.INFO, "refund sent userId={0,number,#} amountCents={1,number,#} accepted={2}",
                payee.id(), amountCents, isAccepted);
        return isAccepted;
    }

    private static boolean isTransaction(String transactionId) {
        return transactionId != null && !transactionId.isBlank();
    }
}
