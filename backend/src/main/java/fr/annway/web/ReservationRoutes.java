package fr.annway.web;

import fr.annway.equipment.Equipment;
import fr.annway.equipment.EquipmentFactory;
import fr.annway.equipment.EquipmentType;
import fr.annway.payment.PaymentService;
import fr.annway.trip.Reservation;
import java.time.Clock;
import java.util.List;
import java.util.Objects;

/**
 * Réservations : le passager demande des places, paie et annule ; le conducteur valide ou
 * refuse. Chacun n'agit que sur ce qui est à lui (sinon 403).
 */
public final class ReservationRoutes {

    /** Séparateur du champ répété {@code equipment=SKI:Skis de 170 cm}. */
    static final String EQUIPMENT_SEPARATOR = ":";

    private final AnnwayStore store;
    private final PaymentService paymentService;
    private final Clock clock;

    public ReservationRoutes(AnnwayStore store, PaymentService paymentService, Clock clock) {
        this.store = Objects.requireNonNull(store, "store");
        this.paymentService = Objects.requireNonNull(paymentService, "paymentService");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public List<Route> routes() {
        return List.of(
                Route.post("/api/trips/{id}/reservations", Access.PASSENGER, this::book),
                Route.get("/api/me/reservations", Access.MEMBER, this::mine),
                Route.post("/api/reservations/{id}/pay", Access.PASSENGER, this::pay),
                Route.post("/api/reservations/{id}/cancel", Access.PASSENGER, this::cancel),
                Route.post("/api/reservations/{id}/validate", Access.DRIVER, this::validate),
                Route.post("/api/reservations/{id}/refuse", Access.DRIVER, this::refuse));
    }

    private ApiResponse book(ApiCall call) {
        var trip = store.requireTrip(call.pathId("id"));
        var form = call.form();
        var equipment = form.all("equipment").stream().map(ReservationRoutes::equipmentOf).toList();
        var reservation = call.passenger().book(store.nextReservationId(), trip,
                form.integer("seatCount"), equipment, clock);
        store.addReservation(reservation);
        return ApiResponse.created(TripViews.reservation(reservation));
    }

    private static Equipment equipmentOf(String field) {
        var parts = field.split(EQUIPMENT_SEPARATOR, 2);
        if (parts.length != 2) {
            throw WebException.invalidField("equipment");
        }
        try {
            return EquipmentFactory.create(EquipmentType.valueOf(parts[0]), parts[1]);
        } catch (IllegalArgumentException unknownType) {
            throw WebException.invalidField("equipment");
        }
    }

    private ApiResponse mine(ApiCall call) {
        return ApiResponse.ok(store.reservationsOf(call.member()).stream().map(TripViews::reservation).toList());
    }

    private ApiResponse pay(ApiCall call) {
        var reservation = ownReservation(call);
        reservation.pay(call.form().longValue("amountCents"), paymentService, clock);
        return ApiResponse.ok(TripViews.reservation(reservation));
    }

    private ApiResponse cancel(ApiCall call) {
        var reservation = ownReservation(call);
        reservation.cancel(call.passenger());
        return ApiResponse.ok(TripViews.reservation(reservation));
    }

    private ApiResponse validate(ApiCall call) {
        var reservation = reservationOnOwnTrip(call);
        call.driver().validateReservation(reservation);
        return ApiResponse.ok(TripViews.reservation(reservation));
    }

    private ApiResponse refuse(ApiCall call) {
        var reservation = reservationOnOwnTrip(call);
        call.driver().refuseReservation(reservation);
        return ApiResponse.ok(TripViews.reservation(reservation));
    }

    private Reservation ownReservation(ApiCall call) {
        var reservation = store.requireReservation(call.pathId("id"));
        if (reservation.passenger().id() != call.passenger().id()) {
            throw new WebException(WebError.FORBIDDEN);
        }
        return reservation;
    }

    private Reservation reservationOnOwnTrip(ApiCall call) {
        var reservation = store.requireReservation(call.pathId("id"));
        if (reservation.trip().driver().id() != call.driver().id()) {
            throw new WebException(WebError.FORBIDDEN);
        }
        return reservation;
    }
}
