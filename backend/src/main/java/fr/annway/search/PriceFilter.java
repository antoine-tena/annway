package fr.annway.search;

import fr.annway.trip.Trip;
import java.util.List;
import java.util.Objects;

/**
 * Garde les trajets dont le montant dû pour une place ne dépasse pas `maxPriceCents`, borne
 * comprise ([MONEY]). Un trajet Polytech, à don libre, ne doit rien : il est toujours gardé.
 */
public record PriceFilter(long maxPriceCents) implements TripFilter {

    public PriceFilter {
        if (maxPriceCents < 0) {
            throw new SearchException(SearchError.NEGATIVE_MAX_PRICE);
        }
    }

    @Override
    public List<Trip> filter(List<Trip> trips) {
        return Objects.requireNonNull(trips, "trips").stream()
                .filter(trip -> trip.amountDueCents(1) <= maxPriceCents)
                .toList();
    }
}
