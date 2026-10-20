package fr.annway.web;

import static fr.annway.web.WebFixtures.body;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuthRoutesTest {

    private final WebFixtures web = new WebFixtures();

    @Test
    void registeringOpensASessionOnTheNewAccount() {
        var token = web.passenger("lea");
        var me = web.get("/api/me", token);
        assertEquals(HttpStatus.OK, me.status());
        assertTrue(body(me).contains("\"email\":\"lea@example.org\""));
        assertTrue(body(me).contains("\"role\":\"PASSENGER\""));
    }

    @Test
    void driverRegistersWithAVehicle() {
        var me = body(web.get("/api/me", web.driver("hugo")));
        assertTrue(me.contains("\"registration\":\"AA-111-AA\""));
    }

    @Test
    void driverWithoutVehicleIsRefusedAndNoAccountIsCreated() {
        var response = web.post("/api/auth/register", Optional.empty(),
                "role=DRIVER&firstName=Hugo&lastName=T&email=hugo@example.org&phone=06&password=" + WebFixtures.PASSWORD);
        assertEquals(HttpStatus.BAD_REQUEST, response.status());
        assertTrue(body(response).contains("\"field\":\"registration\""));
        web.driver("hugo");
    }

    @Test
    void emailCanOnlyBeUsedOnce() {
        web.passenger("lea");
        var again = web.post("/api/auth/register", Optional.empty(),
                "role=PASSENGER&firstName=Lea&lastName=T&email=LEA@example.org&phone=06&password=" + WebFixtures.PASSWORD);
        assertEquals(HttpStatus.CONFLICT, again.status());
    }

    @Test
    void shortPasswordIsRefused() {
        var response = web.post("/api/auth/register", Optional.empty(),
                "role=PASSENGER&firstName=Lea&lastName=T&email=lea@example.org&phone=06&password=court");
        assertEquals(HttpStatus.BAD_REQUEST, response.status());
        assertTrue(body(response).contains("WEAK_PASSWORD"));
    }

    @Test
    void loginChecksThePassword() {
        web.passenger("lea");
        var refused = web.post("/api/auth/login", Optional.empty(), "email=lea@example.org&password=mauvais-mot");
        assertEquals(HttpStatus.UNAUTHORIZED, refused.status());
        var accepted = web.post("/api/auth/login", Optional.empty(),
                "email=lea@example.org&password=" + WebFixtures.PASSWORD);
        assertEquals(HttpStatus.OK, accepted.status());
        assertEquals(HttpStatus.OK, web.get("/api/me", WebFixtures.tokenOf(accepted)).status());
    }

    @Test
    void sessionCookieIsHostOnlyHttpOnlyAndSecure() {
        var response = web.post("/api/auth/register", Optional.empty(),
                "role=PASSENGER&firstName=Lea&lastName=T&email=lea@example.org&phone=06&password=" + WebFixtures.PASSWORD);
        var cookie = response.cookies().getFirst();
        assertTrue(cookie.startsWith("__Host-annway_session="));
        assertTrue(cookie.contains("HttpOnly") && cookie.contains("Secure") && cookie.contains("SameSite=Lax"));
    }

    @Test
    void logoutClosesTheSession() {
        var token = web.passenger("lea");
        var response = web.post("/api/auth/logout", token, "");
        assertEquals(HttpStatus.NO_CONTENT, response.status());
        assertTrue(response.cookies().getFirst().contains("Max-Age=0"));
        assertEquals(HttpStatus.UNAUTHORIZED, web.get("/api/me", token).status());
    }
}
