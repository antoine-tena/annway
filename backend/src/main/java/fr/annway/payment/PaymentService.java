package fr.annway.payment;

import fr.annway.account.User;

/** Service de paiement vu par annway : débite un utilisateur d'un montant en centimes ([ARGENT]). */
public interface PaymentService {

    /** Vrai si le paiement est accepté. */
    boolean pay(User payer, long amountCents);
}
