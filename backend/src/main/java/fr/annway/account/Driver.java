package fr.annway.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Conducteur : possède au moins un véhicule, deux véhicules ne partagent pas une immatriculation.
 * La publication de trajets et la gestion des réservations s'ajoutent quand les classes des
 * trajets existent.
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
}
