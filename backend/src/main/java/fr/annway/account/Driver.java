package fr.annway.account;

import fr.annway.search.TripCatalogue;
import fr.annway.trip.Reservation;
import fr.annway.trip.Trip;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Conducteur : possède au moins un véhicule, deux véhicules ne partagent pas une immatriculation.
 * Il publie ses trajets au catalogue et valide ou refuse les réservations qu'on lui demande ; les
 * contrôles (son trajet, une seule publication, réservation en attente) sont faits par le
 * catalogue et la réservation.
 */
public final class Driver extends User {

    private final List<Vehicle> vehicles = new ArrayList<>();

    public Driver(int id, String firstName, String lastName, String email, String phone, List<Vehicle> vehicles) {
        super(id, firstName, lastName, email, phone);
        Objects.requireNonNull(vehicles, "vehicles");
        if (vehicles.isEmpty()) {
            throw new AccountException(AccountError.DRIVER_WITHOUT_VEHICLE);
        }
        vehicles.forEach(this::addVehicle);
    }

    public void addVehicle(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "vehicle");
        var isAlreadyRegistered = vehicles.stream()
                .anyMatch(owned -> owned.registration().equals(vehicle.registration()));
        if (isAlreadyRegistered) {
            throw new AccountException(AccountError.VEHICLE_ALREADY_REGISTERED);
        }
        vehicles.add(vehicle);
    }

    public List<Vehicle> vehicles() {
        return List.copyOf(vehicles);
    }

    /** Publie son trajet au catalogue, qui prévient les alertes abonnées. */
    public void publishTrip(Trip trip, TripCatalogue catalogue) {
        Objects.requireNonNull(catalogue, "catalogue").publish(this, trip);
    }

    /** Accepte la réservation : les places sont prises sur le trajet. */
    public void validateReservation(Reservation reservation) {
        Objects.requireNonNull(reservation, "reservation").validate(this);
    }

    public void refuseReservation(Reservation reservation) {
        Objects.requireNonNull(reservation, "reservation").refuse(this);
    }
}
