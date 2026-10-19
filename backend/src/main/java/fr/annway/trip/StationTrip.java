package fr.annway.trip;

import fr.annway.account.Driver;
import fr.annway.account.Vehicle;

/** Trajet direct vers la station : chaque place a un prix fixé par le conducteur, à payer. */
public final class StationTrip extends Trip {

    private final long pricePerSeatCents;

    public StationTrip(TripRoute route, Driver driver, Vehicle vehicle, long pricePerSeatCents) {
        super(route, driver, vehicle);
        if (pricePerSeatCents < 1) {
            throw new TripException(TripError.INVALID_PRICE);
        }
        this.pricePerSeatCents = pricePerSeatCents;
    }

    public long pricePerSeatCents() {
        return pricePerSeatCents;
    }

    @Override
    public TripType type() {
        return TripType.STATION;
    }

    @Override
    public boolean isPaymentRequired() {
        return true;
    }

    @Override
    public long amountDueCents(int seatCount) {
        requirePositiveSeatCount(seatCount);
        return Math.multiplyExact(pricePerSeatCents, seatCount);
    }
}
