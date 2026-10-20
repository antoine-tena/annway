package fr.annway.web;

import fr.annway.equipment.Equipment;
import fr.annway.trip.Payment;
import fr.annway.trip.Refund;
import fr.annway.trip.PolytechTrip;
import fr.annway.trip.Reservation;
import fr.annway.trip.StationTrip;
import fr.annway.trip.Stop;
import fr.annway.trip.Trip;
import java.util.Optional;

/** Trajets et réservations en JSON ; montants en centimes ([MONEY]), instants en UTC ISO-8601. */
public final class TripViews {

    private TripViews() {
    }

    public static JsonObject summary(Trip trip) {
        var json = new JsonObject()
                .put("id", trip.id())
                .put("type", trip.type())
                .put("status", trip.status())
                .put("departure", trip.departure())
                .put("arrival", trip.arrival())
                .put("departureAt", trip.departureAt().toString())
                .put("availableSeats", trip.availableSeats())
                .put("seatCount", trip.vehicle().seatCount())
                .put("remainingEquipmentCapacity", trip.remainingEquipmentCapacity())
                .put("isPaymentRequired", trip.isPaymentRequired())
                .put("pricePerSeatCents", trip.amountDueCents(1))
                .put("driver", AccountViews.summary(trip.driver()));
        return switch (trip) {
            case StationTrip ignored -> json;
            case PolytechTrip polytech -> json
                    .put("servedStation", polytech.servedStation())
                    .put("busDepartureAt", polytech.busDepartureAt().toString());
        };
    }

    public static JsonObject detail(Trip trip) {
        return summary(trip)
                .put("vehicleModel", trip.vehicle().model())
                .put("stops", trip.stops().stream().map(TripViews::stop).toList());
    }

    private static JsonObject stop(Stop stop) {
        return new JsonObject().put("place", stop.place()).put("passingAt", stop.passingAt().toString());
    }

    public static JsonObject reservation(Reservation reservation) {
        return new JsonObject()
                .put("id", reservation.id())
                .put("trip", summary(reservation.trip()))
                .put("passenger", AccountViews.summary(reservation.passenger()))
                .put("seatCount", reservation.seatCount())
                .put("equipment", reservation.equipment().stream().map(TripViews::equipment).toList())
                .put("requestedAt", reservation.requestedAt().toString())
                .put("status", reservation.status())
                .put("amountDueCents", reservation.trip().amountDueCents(reservation.seatCount()))
                .put("payment", payment(reservation.payment()))
                .put("refund", refund(reservation.refund()))
                .put("refundDueCents", reservation.refundDueCents());
    }

    public static JsonObject equipment(Equipment equipment) {
        return new JsonObject()
                .put("type", equipment.type())
                .put("description", equipment.description())
                .put("bulk", equipment.bulk());
    }

    private static Optional<JsonObject> payment(Optional<Payment> payment) {
        return payment.map(paid -> new JsonObject()
                .put("amountCents", paid.amountCents())
                .put("type", paid.type())
                .put("paidAt", paid.paidAt().toString()));
    }

    private static Optional<JsonObject> refund(Optional<Refund> refund) {
        return refund.map(refunded -> new JsonObject()
                .put("amountCents", refunded.amountCents())
                .put("refundedAt", refunded.refundedAt().toString()));
    }
}
