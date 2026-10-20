package fr.annway.payment;

import fr.annway.account.User;

/** Remboursement vu par annway : rend à un utilisateur un montant en centimes ([MONEY]). */
public interface RefundService {

    /** Vrai si le remboursement est accepté. */
    boolean refund(User payee, long amountCents);
}
