package fr.annway.web;

import java.time.Clock;
import java.util.List;
import java.util.Objects;

/** Messages échangés sous l'annonce d'un trajet, réservés aux membres connectés. */
public final class MessageRoutes {

    private final AnnwayStore store;
    private final Clock clock;

    public MessageRoutes(AnnwayStore store, Clock clock) {
        this.store = Objects.requireNonNull(store, "store");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public List<Route> routes() {
        return List.of(
                Route.get("/api/trips/{id}/messages", Access.MEMBER, this::list),
                Route.post("/api/trips/{id}/messages", Access.MEMBER, this::send));
    }

    private ApiResponse list(ApiCall call) {
        var trip = store.requireTrip(call.pathId("id"));
        return ApiResponse.ok(store.messagesOf(trip).stream().map(SearchViews::message).toList());
    }

    private ApiResponse send(ApiCall call) {
        var trip = store.requireTrip(call.pathId("id"));
        var message = call.member().sendMessage(trip, call.form().text("content"), clock);
        store.addMessage(message);
        return ApiResponse.created(SearchViews.message(message));
    }
}
