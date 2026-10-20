package fr.annway.payment;

/**
 * Client de l'API de paiement tierce, avec son propre contrat : le payeur est désigné par son
 * e-mail, la réponse est l'identifiant de la transaction, vide si l'opération est refusée.
 */
public interface ExternalPaymentApi {

    String sendPayment(String email, long amountCents);

    String sendRefund(String email, long amountCents);
}
