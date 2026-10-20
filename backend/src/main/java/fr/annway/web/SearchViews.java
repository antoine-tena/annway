package fr.annway.web;

import fr.annway.account.Message;
import fr.annway.search.TripAlert;
import fr.annway.trip.SearchCriteria;
import fr.annway.trip.TripRequest;

/** Critères, alertes, demandes de trajet et messages en JSON. */
public final class SearchViews {

    private SearchViews() {
    }

    public static JsonObject criteria(SearchCriteria criteria) {
        return new JsonObject()
                .put("departure", criteria.departure())
                .put("arrival", criteria.arrival())
                .put("date", criteria.date().toString())
                .put("passengerCount", criteria.passengerCount())
                .put("type", criteria.type());
    }

    /** Alerte d'un passager, numérotée dans l'ordre de création : le numéro sert à la basculer. */
    public static JsonObject alert(TripAlert alert, int index) {
        return new JsonObject()
                .put("index", index)
                .put("isActive", alert.isActive())
                .put("criteria", criteria(alert.criteria()))
                .put("matchedTrips", alert.matchedTrips().stream().map(TripViews::summary).toList());
    }

    public static JsonObject tripRequest(TripRequest request) {
        return new JsonObject()
                .put("passenger", AccountViews.summary(request.passenger()))
                .put("criteria", criteria(request.criteria()))
                .put("createdOn", request.createdOn().toString());
    }

    public static JsonObject message(Message message) {
        return new JsonObject()
                .put("sender", AccountViews.summary(message.sender()))
                .put("content", message.content())
                .put("sentAt", message.sentAt().toString());
    }
}
