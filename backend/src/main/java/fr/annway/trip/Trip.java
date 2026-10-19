package fr.annway.trip;

import fr.annway.account.Driver;
import fr.annway.account.Vehicle;
import fr.annway.equipment.Equipment;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Trajet publié par un conducteur avec l'un de ses véhicules. Les places et la capacité de
 * matériel disponibles partent de celles du véhicule. Le départ est un instant UTC ; le jour du
 * départ se lit dans le calendrier de Paris ([FUSEAU]).
 */
public abstract sealed class Trip permits StationTrip, PolytechTrip {

    static final ZoneId PARIS = ZoneId.of("Europe/Paris");

    private final int id;
    private final Driver driver;
    private final Vehicle vehicle;
    private final String departure;
    private final String arrival;
    private final Instant departureAt;
    private final List<Stop> stops = new ArrayList<>();
    private int availableSeats;
    private int remainingEquipmentCapacity;
    private TripStatus status = TripStatus.OPEN;

    protected Trip(TripRoute route, Driver driver, Vehicle vehicle) {
        Objects.requireNonNull(route, "route");
        this.driver = Objects.requireNonNull(driver, "driver");
        this.vehicle = Objects.requireNonNull(vehicle, "vehicle");
        if (!driver.vehicles().contains(vehicle)) {
            throw new TripException(TripError.VEHICLE_NOT_OWNED);
        }
        this.id = route.id();
        this.departure = route.departure();
        this.arrival = route.arrival();
        this.departureAt = route.departureAt();
        this.availableSeats = vehicle.seatCount();
        this.remainingEquipmentCapacity = vehicle.equipmentCapacity();
    }

    public int id() {
        return id;
    }

    public Driver driver() {
        return driver;
    }

    public Vehicle vehicle() {
        return vehicle;
    }

    public String departure() {
        return departure;
    }

    public String arrival() {
        return arrival;
    }

    public Instant departureAt() {
        return departureAt;
    }

    public int availableSeats() {
        return availableSeats;
    }

    public int remainingEquipmentCapacity() {
        return remainingEquipmentCapacity;
    }

    public TripStatus status() {
        return status;
    }

    public List<Stop> stops() {
        return List.copyOf(stops);
    }

    /** Ajoute une étape, qui ne peut pas précéder le départ. */
    public void addStop(Stop stop) {
        Objects.requireNonNull(stop, "stop");
        if (stop.passingAt().isBefore(departureAt)) {
            throw new TripException(TripError.STOP_BEFORE_DEPARTURE);
        }
        stops.add(stop);
    }

    /** Vrai si le trajet ouvert peut encore prendre ces places et ce matériel. */
    public boolean hasRoomFor(int seatCount, List<Equipment> equipment) {
        requirePositiveSeatCount(seatCount);
        Objects.requireNonNull(equipment, "equipment");
        var bulk = equipment.stream().mapToInt(Equipment::bulk).sum();
        return status == TripStatus.OPEN
                && seatCount <= availableSeats
                && bulk <= remainingEquipmentCapacity;
    }

    /**
     * Vrai si le trajet est ouvert et répond aux critères : même départ et même arrivée (sans
     * tenir compte de la casse), départ le jour demandé à Paris, assez de places, et le type
     * demandé s'il y en a un.
     */
    public boolean matches(SearchCriteria criteria) {
        Objects.requireNonNull(criteria, "criteria");
        return status == TripStatus.OPEN
                && departure.equalsIgnoreCase(criteria.departure())
                && arrival.equalsIgnoreCase(criteria.arrival())
                && departureAt.atZone(PARIS).toLocalDate().equals(criteria.date())
                && criteria.passengerCount() <= availableSeats
                && criteria.type().map(wanted -> wanted == type()).orElse(true);
    }

    /** Annule le trajet : il sort des recherches et ne prend plus de réservation. */
    public void cancel() {
        status = TripStatus.CANCELLED;
    }

    public abstract TripType type();

    /** Vrai si le passager doit payer pour que sa réservation soit validée. */
    public abstract boolean isPaymentRequired();

    /** Montant dû pour `seatCount` places, en centimes ([ARGENT]) ; 0 quand le don est libre. */
    public abstract long amountDueCents(int seatCount);

    static void requirePositiveSeatCount(int seatCount) {
        if (seatCount < 1) {
            throw new TripException(TripError.INVALID_SEAT_COUNT);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + "]";
    }
}
