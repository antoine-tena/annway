package fr.annway.search;

import fr.annway.trip.Trip;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

/**
 * Garde les trajets qui partent entre `earliest` et `latest`, bornes comprises, à l'heure de Paris
 * ([TIME-ZONE]) : le départ, stocké en UTC, est lu dans le fuseau de Paris avant la comparaison.
 */
public record TimeWindowFilter(LocalTime earliest, LocalTime latest) implements TripFilter {

    static final ZoneId PARIS = ZoneId.of("Europe/Paris");

    public TimeWindowFilter {
        Objects.requireNonNull(earliest, "earliest");
        Objects.requireNonNull(latest, "latest");
        if (earliest.isAfter(latest)) {
            throw new SearchException(SearchError.INVERTED_TIME_WINDOW);
        }
    }

    @Override
    public List<Trip> filter(List<Trip> trips) {
        return Objects.requireNonNull(trips, "trips").stream()
                .filter(this::leavesWithinWindow)
                .toList();
    }

    private boolean leavesWithinWindow(Trip trip) {
        var departureTime = trip.departureAt().atZone(PARIS).toLocalTime();
        return !departureTime.isBefore(earliest) && !departureTime.isAfter(latest);
    }
}
