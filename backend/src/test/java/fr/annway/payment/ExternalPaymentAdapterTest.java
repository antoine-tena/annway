package fr.annway.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.annway.account.Passenger;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExternalPaymentAdapterTest {

    private static final Passenger PAYER = new Passenger(7, "Ada", "Lovelace", "ada@example.org", "0600000000");

    @Test
    void forwardsEmailAndCentsToTheApi() {
        List<String> calls = new ArrayList<>();
        var adapter = new ExternalPaymentAdapter((email, amountCents) -> {
            calls.add(email + ":" + amountCents);
            return "tx-1";
        });
        assertTrue(adapter.pay(PAYER, 1500));
        assertEquals(List.of("ada@example.org:1500"), calls);
    }

    @Test
    void blankTransactionMeansRefused() {
        assertFalse(new ExternalPaymentAdapter((email, amountCents) -> " ").pay(PAYER, 1500));
    }

    @Test
    void missingTransactionMeansRefused() {
        assertFalse(new ExternalPaymentAdapter((email, amountCents) -> null).pay(PAYER, 1500));
    }
}
