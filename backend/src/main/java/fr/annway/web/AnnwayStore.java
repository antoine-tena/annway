package fr.annway.web;

import fr.annway.account.Driver;
import fr.annway.account.Message;
import fr.annway.account.Passenger;
import fr.annway.account.User;
import fr.annway.search.TripAlert;
import fr.annway.search.TripCatalogue;
import fr.annway.trip.Reservation;
import fr.annway.trip.Trip;
import fr.annway.trip.TripRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;

/**
 * Stockage en mémoire des objets du domaine, le temps que le projet ait une base de données :
 * comptes, catalogue des trajets, réservations, messages, alertes et demandes de trajet. Les
 * identifiants sont attribués ici. Le serveur traite une requête à la fois, d'où l'absence de
 * verrou.
 */
public final class AnnwayStore {

    private final Map<Integer, User> users = new LinkedHashMap<>();
    private final TripCatalogue catalogue = new TripCatalogue();
    private final Map<Integer, Reservation> reservations = new LinkedHashMap<>();
    private final Map<Integer, List<Message>> messagesByTrip = new LinkedHashMap<>();
    private final Map<Integer, List<TripAlert>> alertsByPassenger = new LinkedHashMap<>();
    private final List<TripRequest> tripRequests = new ArrayList<>();
    private int lastUserId;
    private int lastTripId;
    private int lastReservationId;

    /** Crée un compte avec le prochain identifiant libre. */
    public <U extends User> U addUser(IntFunction<U> factory) {
        var user = factory.apply(lastUserId + 1);
        lastUserId = user.id();
        users.put(user.id(), user);
        return user;
    }

    public Optional<User> user(int id) {
        return Optional.ofNullable(users.get(id));
    }

    public User requireUser(int id) {
        return user(id).orElseThrow(() -> new WebException(WebError.NOT_FOUND));
    }

    public TripCatalogue catalogue() {
        return catalogue;
    }

    public int nextTripId() {
        return ++lastTripId;
    }

    public Trip requireTrip(int id) {
        return catalogue.trips().stream()
                .filter(trip -> trip.id() == id)
                .findFirst()
                .orElseThrow(() -> new WebException(WebError.NOT_FOUND));
    }

    /** Trajets du catalogue, par heure de départ. */
    public List<Trip> trips() {
        return catalogue.trips().stream().sorted(Comparator.comparing(Trip::departureAt)).toList();
    }

    public List<Trip> tripsOf(Driver driver) {
        return trips().stream().filter(trip -> trip.driver().id() == driver.id()).toList();
    }

    public int nextReservationId() {
        return ++lastReservationId;
    }

    public void addReservation(Reservation reservation) {
        reservations.put(reservation.id(), Objects.requireNonNull(reservation, "reservation"));
    }

    public Reservation requireReservation(int id) {
        return Optional.ofNullable(reservations.get(id))
                .orElseThrow(() -> new WebException(WebError.NOT_FOUND));
    }

    public List<Reservation> reservationsOn(Trip trip) {
        return reservations.values().stream()
                .filter(reservation -> reservation.trip().id() == trip.id())
                .toList();
    }

    /** Réservations d'un passager, ou reçues sur les trajets d'un conducteur. */
    public List<Reservation> reservationsOf(User user) {
        return reservations.values().stream()
                .filter(reservation -> reservation.passenger().id() == user.id()
                        || reservation.trip().driver().id() == user.id())
                .toList();
    }

    public void addMessage(Message message) {
        messagesByTrip.computeIfAbsent(message.trip().id(), ignored -> new ArrayList<>()).add(message);
    }

    public List<Message> messagesOf(Trip trip) {
        return List.copyOf(messagesByTrip.getOrDefault(trip.id(), List.of()));
    }

    public void addAlert(TripAlert alert) {
        alertsByPassenger.computeIfAbsent(alert.passenger().id(), ignored -> new ArrayList<>()).add(alert);
    }

    public List<TripAlert> alertsOf(Passenger passenger) {
        return List.copyOf(alertsByPassenger.getOrDefault(passenger.id(), List.of()));
    }

    public void addTripRequest(TripRequest request) {
        tripRequests.add(Objects.requireNonNull(request, "request"));
    }

    public List<TripRequest> tripRequests() {
        return List.copyOf(tripRequests);
    }
}
