package fr.annway.web;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/** Alertes de trajet d'un passager et demandes de trajet, que les conducteurs consultent. */
public final class SearchRoutes {

    private final AnnwayStore store;
    private final Clock clock;

    public SearchRoutes(AnnwayStore store, Clock clock) {
        this.store = Objects.requireNonNull(store, "store");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public List<Route> routes() {
        return List.of(
                Route.get("/api/me/alerts", Access.PASSENGER, this::alerts),
                Route.post("/api/me/alerts", Access.PASSENGER, this::subscribe),
                Route.post("/api/me/alerts/{index}/toggle", Access.PASSENGER, this::toggle),
                Route.get("/api/me/trip-requests", Access.PASSENGER, this::myRequests),
                Route.post("/api/me/trip-requests", Access.PASSENGER, this::createRequest),
                Route.get("/api/trip-requests", Access.DRIVER, this::allRequests));
    }

    private ApiResponse alerts(ApiCall call) {
        var alerts = store.alertsOf(call.passenger());
        return ApiResponse.ok(IntStream.range(0, alerts.size())
                .mapToObj(index -> SearchViews.alert(alerts.get(index), index))
                .toList());
    }

    private ApiResponse subscribe(ApiCall call) {
        var passenger = call.passenger();
        var alert = passenger.subscribeAlert(CriteriaForm.criteria(call.form()), store.catalogue());
        store.addAlert(alert);
        return ApiResponse.created(SearchViews.alert(alert, store.alertsOf(passenger).size() - 1));
    }

    private ApiResponse toggle(ApiCall call) {
        var alerts = store.alertsOf(call.passenger());
        var index = call.pathId("index");
        if (index < 0 || index >= alerts.size()) {
            throw new WebException(WebError.NOT_FOUND);
        }
        var alert = alerts.get(index);
        if (alert.isActive()) {
            alert.deactivate();
        } else {
            alert.activate();
        }
        return ApiResponse.ok(SearchViews.alert(alert, index));
    }

    private ApiResponse myRequests(ApiCall call) {
        var passengerId = call.passenger().id();
        return ApiResponse.ok(store.tripRequests().stream()
                .filter(request -> request.passenger().id() == passengerId)
                .map(SearchViews::tripRequest)
                .toList());
    }

    private ApiResponse createRequest(ApiCall call) {
        var request = call.passenger().createTripRequest(CriteriaForm.criteria(call.form()), clock);
        store.addTripRequest(request);
        return ApiResponse.created(SearchViews.tripRequest(request));
    }

    private ApiResponse allRequests(ApiCall call) {
        return ApiResponse.ok(store.tripRequests().stream().map(SearchViews::tripRequest).toList());
    }
}
