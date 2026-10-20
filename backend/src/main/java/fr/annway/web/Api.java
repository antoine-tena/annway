package fr.annway.web;

import fr.annway.payment.PaymentService;
import fr.annway.payment.RefundService;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Objects;

/** Assemble l'API : stockage, identifiants, sessions, limite de débit et toutes les routes. */
public final class Api {

    private final AnnwayStore store = new AnnwayStore();
    private final Credentials credentials;
    private final Router router;

    public Api(Clock clock, SecureRandom random, PaymentService paymentService, RefundService refundService,
            int passwordIterations) {
        Objects.requireNonNull(clock, "clock");
        this.credentials = new Credentials(new PasswordHasher(random, passwordIterations));
        var sessions = new SessionStore(random, clock);
        var routes = new ArrayList<Route>();
        routes.addAll(MetaRoutes.routes());
        routes.addAll(new AuthRoutes(store, credentials, sessions).routes());
        routes.addAll(new AccountRoutes(store, clock).routes());
        routes.addAll(new TripRoutes(store, refundService, clock).routes());
        routes.addAll(new ReservationRoutes(store, paymentService, refundService, clock).routes());
        routes.addAll(new SearchRoutes(store, clock).routes());
        routes.addAll(new MessageRoutes(store, clock).routes());
        var publicLimiter = new RateLimiter(clock, RateLimiter.PUBLIC_LIMIT_PER_MINUTE);
        this.router = new Router(routes, sessions, store, publicLimiter);
    }

    public AnnwayStore store() {
        return store;
    }

    public Credentials credentials() {
        return credentials;
    }

    public Router router() {
        return router;
    }
}
