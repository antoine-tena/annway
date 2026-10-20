package fr.annway.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.annway.payment.PaymentService;
import fr.annway.payment.RefundService;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * Client de test de l'API, sans réseau ([NO-NETWORK]) : il appelle le routeur directement,
 * avec une horloge fixe et des données fictives.
 */
final class WebFixtures {

    /** Lundi 14 décembre 2026, 9 h à Paris. */
    static final Instant NOW = Instant.parse("2026-12-14T08:00:00Z");
    static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    static final String PASSWORD = "motdepasse-de-test";
    static final String CLIENT = "192.0.2.0";
    static final PaymentService ACCEPTING = (payer, amountCents) -> true;
    static final RefundService REFUNDING = (payee, amountCents) -> true;

    final Api api;

    WebFixtures() {
        this(CLOCK);
    }

    WebFixtures(Clock clock) {
        api = new Api(clock, new SecureRandom(), ACCEPTING, REFUNDING, 1);
    }

    ApiResponse get(String path, Optional<String> token) {
        var pathAndQuery = path.split("\\?", 2);
        var query = pathAndQuery.length == 2 ? FormData.parse(pathAndQuery[1]) : FormData.empty();
        return api.router().handle(new ApiRequest("GET", pathAndQuery[0], query, FormData.empty(), token, CLIENT));
    }

    ApiResponse post(String path, Optional<String> token, String form) {
        return api.router().handle(new ApiRequest("POST", path, FormData.empty(), FormData.parse(form), token, CLIENT));
    }

    /** Inscrit un passager et rend le jeton de sa session. */
    Optional<String> passenger(String firstName) {
        return register("role=PASSENGER&firstName=" + firstName + "&lastName=Test&email=" + firstName
                + "@example.org&phone=0600000000&password=" + PASSWORD);
    }

    /** Inscrit un conducteur avec un véhicule de 3 places (immatriculation AA-111-AA). */
    Optional<String> driver(String firstName) {
        return register("role=DRIVER&firstName=" + firstName + "&lastName=Test&email=" + firstName
                + "@example.org&phone=0600000000&password=" + PASSWORD
                + "&registration=AA-111-AA&model=Clio&seatCount=3&equipmentCapacity=4");
    }

    private Optional<String> register(String form) {
        var response = post("/api/auth/register", Optional.empty(), form);
        assertEquals(HttpStatus.CREATED, response.status(), response.body().orElse(""));
        return tokenOf(response);
    }

    static Optional<String> tokenOf(ApiResponse response) {
        return response.cookies().stream()
                .flatMap(cookie -> SessionCookie.read(cookie.split(";")[0]).stream())
                .findFirst();
    }

    /** Publie un trajet vers une station, après-demain à 7 h 30, à 8 € la place ; rend son id. */
    int stationTrip(Optional<String> driverToken) {
        var response = post("/api/trips", driverToken, "type=STATION&departure=Annecy&arrival=La+Clusaz"
                + "&departureAt=2026-12-16T07:30&registration=AA-111-AA&pricePerSeatCents=800");
        assertEquals(HttpStatus.CREATED, response.status(), response.body().orElse(""));
        return idOf(body(response));
    }

    static String body(ApiResponse response) {
        return response.body().orElseThrow();
    }

    /** Premier {@code "id":N} d'un corps JSON. */
    static int idOf(String json) {
        var start = json.indexOf("\"id\":") + "\"id\":".length();
        var end = start;
        while (Character.isDigit(json.charAt(end))) {
            end++;
        }
        return Integer.parseInt(json.substring(start, end));
    }
}
