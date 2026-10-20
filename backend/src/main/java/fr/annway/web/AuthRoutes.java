package fr.annway.web;

import fr.annway.account.Driver;
import fr.annway.account.Passenger;
import fr.annway.account.User;
import fr.annway.account.Vehicle;
import java.util.List;
import java.util.Objects;

/** Inscription, connexion et déconnexion. */
public final class AuthRoutes {

    private final AnnwayStore store;
    private final Credentials credentials;
    private final SessionStore sessions;

    public AuthRoutes(AnnwayStore store, Credentials credentials, SessionStore sessions) {
        this.store = Objects.requireNonNull(store, "store");
        this.credentials = Objects.requireNonNull(credentials, "credentials");
        this.sessions = Objects.requireNonNull(sessions, "sessions");
    }

    public List<Route> routes() {
        return List.of(
                // Publique : on s'inscrit avant d'avoir un compte (limitée en débit).
                Route.post("/api/auth/register", Access.PUBLIC, this::register),
                // Publique : on se connecte avant d'avoir une session (limitée en débit).
                Route.post("/api/auth/login", Access.PUBLIC, this::login),
                Route.post("/api/auth/logout", Access.MEMBER, this::logout));
    }

    private ApiResponse register(ApiCall call) {
        var form = call.form();
        var role = form.enumValue("role", Role.class);
        var userId = credentials.register(form.text("email"), form.text("password"),
                () -> store.addUser(id -> newUser(id, role, form)).id());
        return openSession(store.requireUser(userId), true);
    }

    private static User newUser(int id, Role role, FormData form) {
        var firstName = form.text("firstName");
        var lastName = form.text("lastName");
        var email = form.text("email");
        var phone = form.text("phone");
        return switch (role) {
            case PASSENGER -> new Passenger(id, firstName, lastName, email, phone);
            case DRIVER -> new Driver(id, firstName, lastName, email, phone, List.of(vehicleOf(form)));
        };
    }

    /** Véhicule décrit par un formulaire (inscription d'un conducteur, ajout d'un véhicule). */
    static Vehicle vehicleOf(FormData form) {
        return new Vehicle(form.text("registration"), form.text("model"),
                form.integer("seatCount"), form.integer("equipmentCapacity"));
    }

    private ApiResponse login(ApiCall call) {
        var form = call.form();
        var userId = credentials.authenticate(form.text("email"), form.text("password"))
                .orElseThrow(() -> new WebException(WebError.INVALID_CREDENTIALS));
        return openSession(store.requireUser(userId), false);
    }

    private ApiResponse openSession(User user, boolean isNewAccount) {
        var token = sessions.open(user.id());
        var body = AccountViews.me(user);
        var response = isNewAccount ? ApiResponse.created(body) : ApiResponse.ok(body);
        return response.withCookie(SessionCookie.open(token));
    }

    private ApiResponse logout(ApiCall call) {
        call.request().sessionToken().ifPresent(sessions::close);
        return ApiResponse.noContent().withCookie(SessionCookie.expired());
    }
}
