package fr.annway.trip;

import fr.annway.account.Driver;
import fr.annway.account.Passenger;
import fr.annway.equipment.Equipment;
import fr.annway.payment.PaymentService;
import fr.annway.payment.RefundService;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Réservation de places sur un trajet, avec le matériel déclaré. Les places ne sont prises qu'à
 * la validation par le conducteur, qui exige le paiement quand le trajet est payant. Créée par
 * {@link #request}.
 *
 * <p>Remboursement d'une réservation payée, prix fixe comme don : entier quand le conducteur la
 * refuse ou annule le trajet, entier quand le passager annule au moins
 * {@link #FREE_CANCELLATION_NOTICE} avant le départ, rien quand il annule plus tard.
 */
public final class Reservation {

    /** Délai avant le départ jusqu'auquel le passager qui annule est remboursé. */
    public static final Duration FREE_CANCELLATION_NOTICE = Duration.ofHours(24);

    private final int id;
    private final Trip trip;
    private final Passenger passenger;
    private final int seatCount;
    private final List<Equipment> equipment;
    private final Instant requestedAt;
    private ReservationStatus status = ReservationStatus.PENDING;
    private Optional<Payment> payment = Optional.empty();
    private long refundDueCents;
    private Optional<Refund> refund = Optional.empty();

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

    public Optional<Refund> refund() {
        return refund;
    }

    /** Montant encore à rembourser, en centimes : 0 une fois remboursé ou sans droit au remboursement. */
    public long refundDueCents() {
        return refund.isPresent() ? 0 : refundDueCents;
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
        refundDueCents = paidCents();
    }

    /**
     * Le passager annule ; une réservation validée rend ses places au trajet. Remboursée si
     * l'annulation arrive assez tôt avant le départ.
     */
    public void cancel(Passenger by, Clock clock) {
        Objects.requireNonNull(by, "by");
        Objects.requireNonNull(clock, "clock");
        if (by.id() != passenger.id()) {
            throw new TripException(TripError.NOT_RESERVATION_PASSENGER);
        }
        requireOpen();
        if (status == ReservationStatus.VALIDATED) {
            trip.releaseSeats(seatCount, totalBulk());
        }
        status = ReservationStatus.CANCELLED;
        var lastFreeCancellation = trip.departureAt().minus(FREE_CANCELLATION_NOTICE);
        refundDueCents = clock.instant().isAfter(lastFreeCancellation) ? 0 : paidCents();
    }

    /** Close la réservation encore ouverte d'un trajet annulé, avec remboursement entier. */
    public void closeForCancelledTrip() {
        if (trip.status() != TripStatus.CANCELLED) {
            throw new TripException(TripError.TRIP_NOT_CANCELLED);
        }
        if (!isOpen()) {
            return;
        }
        status = ReservationStatus.TRIP_CANCELLED;
        refundDueCents = paidCents();
    }

    /**
     * Rembourse ce qui est dû après la fermeture de la réservation ; rien quand rien n'est dû.
     * Le remboursement n'est enregistré que si le service l'accepte.
     */
    public Optional<Refund> refund(RefundService service, Clock clock) {
        Objects.requireNonNull(service, "service");
        Objects.requireNonNull(clock, "clock");
        var amountCents = refundDueCents();
        if (amountCents == 0) {
            return Optional.empty();
        }
        var accepted = new Refund(amountCents, clock.instant());
        if (!service.refund(passenger, amountCents)) {
            throw new TripException(TripError.REFUND_DECLINED);
        }
        refund = Optional.of(accepted);
        return refund;
    }

    private long paidCents() {
        return payment.map(Payment::amountCents).orElse(0L);
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

    private boolean isOpen() {
        return status == ReservationStatus.PENDING || status == ReservationStatus.VALIDATED;
    }

    private void requireOpen() {
        if (!isOpen()) {
            throw new TripException(TripError.RESERVATION_CLOSED);
        }
    }

    @Override
    public String toString() {
        return "Reservation[id=" + id + ", tripId=" + trip.id() + "]";
    }
}
