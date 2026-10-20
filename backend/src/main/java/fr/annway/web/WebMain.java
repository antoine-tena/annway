package fr.annway.web;

import com.sun.net.httpserver.HttpServer;
import fr.annway.payment.ExternalPaymentAdapter;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Map;
import java.util.Optional;

/**
 * Point d'entrée du serveur de l'API, sur le serveur HTTP du JDK. Lit l'environnement à la
 * frontière ([SECRETS]) : {@code ANNWAY_PORT} (8080 par défaut) et
 * {@code ANNWAY_DEMO_PASSWORD}, le mot de passe des comptes de démonstration (facultatif). Une
 * requête est traitée à la fois : le stockage en mémoire n'a pas de verrou.
 */
public final class WebMain {

    static final int DEFAULT_PORT = 8080;
    static final int MAX_PORT = 65_535;
    private static final System.Logger LOG = System.getLogger(WebMain.class.getName());

    private WebMain() {
    }

    public static void main(String[] args) throws IOException {
        var environment = System.getenv();
        var port = port(environment);
        var clock = Clock.systemUTC();
        var api = new Api(clock, new SecureRandom(), new ExternalPaymentAdapter(new DemoPaymentApi()),
                PasswordHasher.DEFAULT_ITERATIONS);
        new DemoData(api.store(), api.credentials(), clock).load(demoPassword(environment));
        var server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/", new HttpEntry(api.router()));
        server.start();
        LOG.log(Level.INFO, "annway API listening port={0,number,#}", port);
    }

    /** Port lu dans l'environnement, validé : un entier de 1 à 65535. */
    static int port(Map<String, String> environment) {
        var text = Optional.ofNullable(environment.get("ANNWAY_PORT")).orElse(String.valueOf(DEFAULT_PORT));
        try {
            var port = Integer.parseInt(text.strip());
            if (port < 1 || port > MAX_PORT) {
                throw new IllegalArgumentException("ANNWAY_PORT hors de 1-65535");
            }
            return port;
        } catch (NumberFormatException malformed) {
            throw new IllegalArgumentException("ANNWAY_PORT n'est pas un entier", malformed);
        }
    }

    static Optional<String> demoPassword(Map<String, String> environment) {
        return Optional.ofNullable(environment.get("ANNWAY_DEMO_PASSWORD")).filter(password -> !password.isBlank());
    }
}
