package fr.annway.trip;

import fr.annway.account.Driver;
import fr.annway.account.Vehicle;
import java.time.Instant;
import java.util.Objects;

/**
 * Trajet vers Polytech pour prendre le bus de la sortie : rien n'est dû, le passager peut faire
 * un don libre.
 */
public final class PolytechTrip extends Trip {

    private final Instant busDepartureAt;
    private final String servedStation;

    public PolytechTrip(TripRoute route, Driver driver, Vehicle vehicle, Instant busDepartureAt,
            String servedStation) {
        super(route, driver, vehicle);
        this.busDepartureAt = Objects.requireNonNull(busDepartureAt, "busDepartureAt");
        this.servedStation = TripException.requireNotBlank(servedStation, "servedStation");
        if (busDepartureAt.isBefore(route.departureAt())) {
            throw new TripException(TripError.BUS_BEFORE_TRIP_DEPARTURE);
        }
    }

    public Instant busDepartureAt() {
        return busDepartureAt;
    }

    /** Station desservie par le bus. */
    public String servedStation() {
        return servedStation;
    }

    @Override
    public TripType type() {
        return TripType.POLYTECH;
    }

    @Override
    public boolean isPaymentRequired() {
        return false;
    }

    @Override
    public long amountDueCents(int seatCount) {
        requirePositiveSeatCount(seatCount);
        return 0;
    }
}
