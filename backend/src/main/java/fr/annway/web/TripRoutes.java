package fr.annway.web;

import fr.annway.account.Driver;
import fr.annway.account.Vehicle;
import fr.annway.payment.RefundService;
import fr.annway.search.PriceFilter;
import fr.annway.search.TimeWindowFilter;
import fr.annway.search.TripFilter;
import fr.annway.trip.PolytechTrip;
import fr.annway.trip.Reservation;
import fr.annway.trip.StationTrip;
import fr.annway.trip.Stop;
import fr.annway.trip.Trip;
import fr.annway.trip.TripRoute;
import fr.annway.trip.TripStatus;
import fr.annway.trip.TripType;
import java.time.Clock;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

/** Catalogue des trajets : recherche, détail, publication et vie d'un trajet par son conducteur. */
public final class TripRoutes {

    private final AnnwayStore store;
    private final RefundService refundService;
    private final Clock clock;

    public TripRoutes(AnnwayStore store, RefundService refundService, Clock clock) {
        this.store = Objects.requireNonNull(store, "store");
        this.refundService = Objects.requireNonNull(refundService, "refundService");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public List<Route> routes() {
        return List.of(
                // Publique : le catalogue se consulte avant de créer un compte (limitée en débit).
                Route.get("/api/trips", Access.PUBLIC, this::search),
                // Publique : le détail d'un trajet du catalogue, même raison (limitée en débit).
                Route.get("/api/trips/{id}", Access.PUBLIC, this::detail),
                Route.get("/api/me/trips", Access.DRIVER, this::myTrips),
                Route.post("/api/trips", Access.DRIVER, this::publish),
                Route.post("/api/trips/{id}/stops", Access.DRIVER, this::addStop),
                Route.post("/api/trips/{id}/cancel", Access.DRIVER, this::cancel),
                Route.post("/api/trips/{id}/finish", Access.DRIVER, this::finish));
    }

    /**
     * Avec départ, arrivée et date, la recherche du catalogue ; sans eux, les trajets à venir
     * encore ouverts ou complets. Puis les filtres : prix maximum, plage horaire.
     */
    private ApiResponse search(ApiCall call) {
        var query = call.query();
        var found = CriteriaForm.hasCriteria(query)
                ? store.catalogue().search(CriteriaForm.criteria(query))
                : upcomingTrips();
        for (var filter : filtersOf(query)) {
            found = filter.filter(found);
        }
        return ApiResponse.ok(new JsonObject()
                .put("trips", found.stream().map(TripViews::summary).toList())
                .put("places", places()));
    }

    private List<Trip> upcomingTrips() {
        var now = clock.instant();
        return store.trips().stream()
                .filter(trip -> trip.status() == TripStatus.OPEN || trip.status() == TripStatus.FULL)
                .filter(trip -> trip.departureAt().isAfter(now))
                .toList();
    }

    private static List<TripFilter> filtersOf(FormData query) {
        var filters = new ArrayList<TripFilter>();
        query.optionalLong("maxPriceCents").ifPresent(max -> filters.add(new PriceFilter(max)));
        var earliest = query.optionalTime("earliest");
        var latest = query.optionalTime("latest");
        if (earliest.isPresent() || latest.isPresent()) {
            filters.add(new TimeWindowFilter(earliest.orElse(LocalTime.MIN), latest.orElse(LocalTime.MAX)));
        }
        return filters;
    }

    /** Lieux de départ et d'arrivée connus, pour proposer les choix de la recherche. */
    private JsonObject places() {
        var departures = new TreeSet<String>();
        var arrivals = new TreeSet<String>();
        store.trips().forEach(trip -> {
            departures.add(trip.departure());
            arrivals.add(trip.arrival());
        });
        return new JsonObject().put("departures", List.copyOf(departures)).put("arrivals", List.copyOf(arrivals));
    }

    private ApiResponse detail(ApiCall call) {
        return ApiResponse.ok(TripViews.detail(store.requireTrip(call.pathId("id"))));
    }

    private ApiResponse myTrips(ApiCall call) {
        return ApiResponse.ok(store.tripsOf(call.driver()).stream().map(TripViews::summary).toList());
    }

    private ApiResponse publish(ApiCall call) {
        var driver = call.driver();
        var form = call.form();
        var route = new TripRoute(store.nextTripId(), form.text("departure"), form.text("arrival"),
                CriteriaForm.parisInstant(form, "departureAt"));
        var vehicle = vehicleOf(driver, form.text("registration"));
        Trip trip = switch (form.enumValue("type", TripType.class)) {
            case STATION -> new StationTrip(route, driver, vehicle, form.longValue("pricePerSeatCents"));
            case POLYTECH -> new PolytechTrip(route, driver, vehicle,
                    CriteriaForm.parisInstant(form, "busDepartureAt"), form.text("servedStation"));
        };
        driver.publishTrip(trip, store.catalogue());
        return ApiResponse.created(TripViews.detail(trip));
    }

    private static Vehicle vehicleOf(Driver driver, String registration) {
        return driver.vehicles().stream()
                .filter(vehicle -> vehicle.registration().equalsIgnoreCase(registration))
                .findFirst()
                .orElseThrow(() -> WebException.invalidField("registration"));
    }

    private ApiResponse addStop(ApiCall call) {
        var trip = ownTrip(call);
        trip.addStop(new Stop(call.form().text("place"), CriteriaForm.parisInstant(call.form(), "passingAt")));
        return ApiResponse.created(TripViews.detail(trip));
    }

    private ApiResponse cancel(ApiCall call) {
        var trip = ownTrip(call);
        trip.cancel();
        var reservations = store.reservationsOn(trip);
        reservations.forEach(Reservation::closeForCancelledTrip);
        reservations.forEach(reservation -> reservation.refund(refundService, clock));
        return ApiResponse.ok(TripViews.detail(trip));
    }

    private ApiResponse finish(ApiCall call) {
        var trip = ownTrip(call);
        trip.finish(clock);
        return ApiResponse.ok(TripViews.detail(trip));
    }

    /** Trajet du chemin, s'il appartient au conducteur connecté (sinon 403). */
    private Trip ownTrip(ApiCall call) {
        var trip = store.requireTrip(call.pathId("id"));
        if (trip.driver().id() != call.driver().id()) {
            throw new WebException(WebError.FORBIDDEN);
        }
        return trip;
    }
}
