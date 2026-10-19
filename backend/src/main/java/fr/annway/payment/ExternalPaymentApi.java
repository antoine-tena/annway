package fr.annway.payment;

/**
 * Client de l'API de paiement tierce, avec son propre contrat : le payeur est désigné par son
 * e-mail, la réponse est l'identifiant de la transaction, vide si le paiement est refusé.
 */
public interface ExternalPaymentApi {

    String sendPayment(String email, long amountCents);
}
