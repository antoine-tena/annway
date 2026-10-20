package fr.annway.web;

import fr.annway.account.Driver;
import fr.annway.account.Passenger;
import fr.annway.account.Vehicle;
import fr.annway.trip.PolytechTrip;
import fr.annway.trip.StationTrip;
import fr.annway.trip.Stop;
import fr.annway.trip.TripRoute;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Données de démonstration, entièrement fictives (e-mails en {@code example.org}) : deux
 * conducteurs, deux passagers et quelques trajets des prochains jours. Les comptes ne se
 * connectent qu'avec le mot de passe de démonstration lu dans l'environnement
 * ({@code ANNWAY_DEMO_PASSWORD}) ; sans lui, ils existent sans pouvoir se connecter.
 */
public final class DemoData {

    static final String ANNECY = "Annecy";
    static final String POLYTECH = "Polytech Annecy";
    static final String BUS_STATION = "Gare routière d'Annecy";
    static final long LA_CLUSAZ_PRICE_CENTS = 700;
    static final long GRAND_BORNAND_PRICE_CENTS = 800;
    static final long SEMNOZ_PRICE_CENTS = 500;
    private static final LocalTime MORNING_DEPARTURE = LocalTime.of(7, 30);
    private static final Duration BUS_WAIT = Duration.ofMinutes(20);
    private static final Duration STOP_DELAY = Duration.ofMinutes(15);

    private final AnnwayStore store;
    private final Credentials credentials;
    private final Clock clock;

    public DemoData(AnnwayStore store, Credentials credentials, Clock clock) {
        this.store = Objects.requireNonNull(store, "store");
        this.credentials = Objects.requireNonNull(credentials, "credentials");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public void load(Optional<String> demoPassword) {
        var camille = store.addUser(id -> new Driver(id, "Camille", "Martin", "camille@example.org",
                "0600000001", List.of(new Vehicle("AA-123-BB", "Peugeot 308", 3, 4))));
        var hugo = store.addUser(id -> new Driver(id, "Hugo", "Bernard", "hugo@example.org",
                "0600000002", List.of(new Vehicle("CC-456-DD", "Renault Trafic", 8, 10))));
        var lea = store.addUser(id -> new Passenger(id, "Léa", "Durand", "lea@example.org", "0600000003"));
        var malo = store.addUser(id -> new Passenger(id, "Malo", "Petit", "malo@example.org", "0600000004"));
        demoPassword.ifPresent(password -> List.of(camille, hugo, lea, malo).forEach(
                user -> credentials.register(user.email(), password, user::id)));
        camille.profile().updateBio("Je monte presque tous les samedis, coffre de toit pour les skis.");
        camille.profile().verifyEmail();
        camille.profile().verifyPhone();
        camille.profile().verifyIdentityDocument();
        lea.leaveReview(camille, 5, "Conduite prudente et ponctuelle.", clock);
        publishTrips(camille, hugo);
    }

    private void publishTrips(Driver camille, Driver hugo) {
        var laClusaz = new StationTrip(route(ANNECY, "La Clusaz", 2), camille,
                camille.vehicles().getFirst(), LA_CLUSAZ_PRICE_CENTS);
        laClusaz.addStop(new Stop("Thônes", laClusaz.departureAt().plus(STOP_DELAY)));
        camille.publishTrip(laClusaz, store.catalogue());
        camille.publishTrip(new StationTrip(route(ANNECY, "Le Grand-Bornand", 5), camille,
                camille.vehicles().getFirst(), GRAND_BORNAND_PRICE_CENTS), store.catalogue());
        hugo.publishTrip(new StationTrip(route(ANNECY, "Semnoz", 3), hugo,
                hugo.vehicles().getFirst(), SEMNOZ_PRICE_CENTS), store.catalogue());
        var toBus = route(POLYTECH, BUS_STATION, 2);
        hugo.publishTrip(new PolytechTrip(toBus, hugo, hugo.vehicles().getFirst(),
                toBus.departureAt().plus(BUS_WAIT), "La Clusaz"), store.catalogue());
    }

    /** Trajet du matin, dans {@code daysAhead} jours, heure de Paris ([TIME-ZONE]). */
    private TripRoute route(String departure, String arrival, int daysAhead) {
        var day = LocalDate.ofInstant(clock.instant(), CriteriaForm.PARIS).plusDays(daysAhead);
        Instant departureAt = day.atTime(MORNING_DEPARTURE).atZone(CriteriaForm.PARIS).toInstant();
        return new TripRoute(store.nextTripId(), departure, arrival, departureAt);
    }
}
