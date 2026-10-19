package fr.annway.trip;

import fr.annway.account.Driver;
import fr.annway.account.Passenger;
import fr.annway.equipment.Equipment;
import fr.annway.payment.PaymentService;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Réservation de places sur un trajet, avec le matériel déclaré. Les places ne sont prises qu'à
 * la validation par le conducteur, qui exige le paiement quand le trajet est payant. Créée par
 * {@link #request}.
 */
public final class Reservation {

    private final int id;
    private final Trip trip;
    private final Passenger passenger;
    private final int seatCount;
    private final List<Equipment> equipment;
    private final Instant requestedAt;
    private ReservationStatus status = ReservationStatus.PENDING;
    private Optional<Payment> payment = Optional.empty();

    private Reservation(int id, Trip trip, Passenger passenger, int seatCount, List<Equipment> equipment,
            Instant requestedAt) {
        this.id = id;
        this.trip = trip;
        this.passenger = passenger;
        this.seatCount = seatCount;
        this.equipment = equipment;
        this.requestedAt = requestedAt;
    }

    /** Demande de réservation, refusée d'emblée si le trajet n'a plus la place. */
    public static Reservation request(int id, Trip trip, Passenger passenger, int seatCount,
            List<Equipment> equipment, Clock clock) {
        if (id < 1) {
            throw new TripException(TripError.INVALID_RESERVATION_ID);
        }
        Objects.requireNonNull(trip, "trip");
        Objects.requireNonNull(passenger, "passenger");
        Objects.requireNonNull(clock, "clock");
        if (!trip.hasRoomFor(seatCount, equipment)) {
            throw new TripException(TripError.NO_ROOM);
        }
        return new Reservation(id, trip, passenger, seatCount, List.copyOf(equipment), clock.instant());
    }

    public int id() {
        return id;
    }

    public Trip trip() {
        return trip;
    }

    public Passenger passenger() {
        return passenger;
    }

    public int seatCount() {
        return seatCount;
    }

    public List<Equipment> equipment() {
        return equipment;
    }

    public Instant requestedAt() {
        return requestedAt;
    }

    public ReservationStatus status() {
        return status;
    }

    public Optional<Payment> payment() {
        return payment;
    }

    public boolean isPaid() {
        return payment.isPresent();
    }

    /** Encombrement total du matériel déclaré, en unités d'encombrement. */
    public int totalBulk() {
        return equipment.stream().mapToInt(Equipment::bulk).sum();
    }

    /** Le conducteur accepte : les places sont prises sur le trajet. */
    public void validate(Driver by) {
        requireTripDriver(by);
        requirePending();
        if (trip.isPaymentRequired() && !isPaid()) {
            throw new TripException(TripError.PAYMENT_REQUIRED);
        }
        trip.takeSeats(seatCount, totalBulk());
        status = ReservationStatus.VALIDATED;
    }

    /** Le conducteur refuse la demande. */
    public void refuse(Driver by) {
        requireTripDriver(by);
        requirePending();
        status = ReservationStatus.REFUSED;
    }

    /** Le passager annule ; une réservation validée rend ses places au trajet. */
    public void cancel(Passenger by) {
        Objects.requireNonNull(by, "by");
        if (by.id() != passenger.id()) {
            throw new TripException(TripError.NOT_RESERVATION_PASSENGER);
        }
        requireOpen();
        if (status == ReservationStatus.VALIDATED) {
            trip.releaseSeats(seatCount, totalBulk());
        }
        status = ReservationStatus.CANCELLED;
    }

    /**
     * Paie la réservation : le prix exact sur un trajet payant, un don libre sinon. Le paiement
     * n'est enregistré que si le service l'accepte.
     */
    public Payment pay(long amountCents, PaymentService service, Clock clock) {
        Objects.requireNonNull(service, "service");
        Objects.requireNonNull(clock, "clock");
        requireOpen();
        if (isPaid()) {
            throw new TripException(TripError.ALREADY_PAID);
        }
        var type = trip.isPaymentRequired() ? PaymentType.FIXED_PRICE : PaymentType.DONATION;
        if (type == PaymentType.FIXED_PRICE && amountCents != trip.amountDueCents(seatCount)) {
            throw new TripException(TripError.WRONG_AMOUNT);
        }
        var accepted = new Payment(amountCents, type, clock.instant());
        if (!service.pay(passenger, amountCents)) {
            throw new TripException(TripError.PAYMENT_DECLINED);
        }
        payment = Optional.of(accepted);
        return accepted;
    }

    private void requireTripDriver(Driver by) {
        Objects.requireNonNull(by, "by");
        if (by.id() != trip.driver().id()) {
            throw new TripException(TripError.NOT_TRIP_DRIVER);
        }
    }

    private void requirePending() {
        if (status != ReservationStatus.PENDING) {
            throw new TripException(TripError.RESERVATION_NOT_PENDING);
        }
    }

    private void requireOpen() {
        if (status == ReservationStatus.REFUSED || status == ReservationStatus.CANCELLED) {
            throw new TripException(TripError.RESERVATION_CLOSED);
        }
    }

    @Override
    public String toString() {
        return "Reservation[id=" + id + ", tripId=" + trip.id() + "]";
    }
}
