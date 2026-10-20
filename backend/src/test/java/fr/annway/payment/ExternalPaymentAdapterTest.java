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

    /** API tierce de test : note chaque appel et rend toujours la même réponse. */
    private static final class RecordingApi implements ExternalPaymentApi {

        private final List<String> calls = new ArrayList<>();
        private final String transactionId;

        RecordingApi(String transactionId) {
            this.transactionId = transactionId;
        }

        @Override
        public String sendPayment(String email, long amountCents) {
            calls.add("pay:" + email + ":" + amountCents);
            return transactionId;
        }

        @Override
        public String sendRefund(String email, long amountCents) {
            calls.add("refund:" + email + ":" + amountCents);
            return transactionId;
        }
    }

    @Test
    void forwardsEmailAndCentsToTheApi() {
        var api = new RecordingApi("tx-1");
        var adapter = new ExternalPaymentAdapter(api);
        assertTrue(adapter.pay(PAYER, 1500));
        assertTrue(adapter.refund(PAYER, 1500));
        assertEquals(List.of("pay:ada@example.org:1500", "refund:ada@example.org:1500"), api.calls);
    }

    @Test
    void blankTransactionMeansRefused() {
        var adapter = new ExternalPaymentAdapter(new RecordingApi(" "));
        assertFalse(adapter.pay(PAYER, 1500));
        assertFalse(adapter.refund(PAYER, 1500));
    }

    @Test
    void missingTransactionMeansRefused() {
        var adapter = new ExternalPaymentAdapter(new RecordingApi(null));
        assertFalse(adapter.pay(PAYER, 1500));
        assertFalse(adapter.refund(PAYER, 1500));
    }
}
