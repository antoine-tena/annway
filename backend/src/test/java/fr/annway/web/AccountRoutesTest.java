package fr.annway.web;

import static fr.annway.web.WebFixtures.body;
import static fr.annway.web.WebFixtures.idOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class AccountRoutesTest {

    private final WebFixtures web = new WebFixtures();
    private final Optional<String> driver = web.driver("hugo");
    private final Optional<String> passenger = web.passenger("lea");
    private final int driverId = idOf(body(web.get("/api/me", driver)));

    @Test
    void memberUpdatesHisBio() {
        var response = web.post("/api/me/profile", passenger, "bio=Snowboardeuse+du+samedi");
        assertTrue(body(response).contains("\"bio\":\"Snowboardeuse du samedi\""));
    }

    @Test
    void driverAddsAVehicleOnlyOnce() {
        var added = web.post("/api/me/vehicles", driver, "registration=bb-222-bb&model=Kangoo&seatCount=4&equipmentCapacity=6");
        assertEquals(HttpStatus.CREATED, added.status());
        assertTrue(body(added).contains("\"registration\":\"BB-222-BB\""));
        var again = web.post("/api/me/vehicles", driver, "registration=BB-222-BB&model=Kangoo&seatCount=4&equipmentCapacity=6");
        assertTrue(body(again).contains("VEHICLE_ALREADY_REGISTERED"));
    }

    @Test
    void publicProfileHidesContactDetails() {
        var profile = body(web.get("/api/users/" + driverId, passenger));
        assertTrue(profile.contains("\"firstName\":\"hugo\""));
        assertFalse(profile.contains("@example.org"));
        assertFalse(profile.contains("\"phone\""));
    }

    @Test
    void reviewThenReplyByTheTarget() {
        var reviewed = web.post("/api/users/" + driverId + "/reviews", passenger, "rating=4&comment=Ponctuel");
        assertEquals(HttpStatus.CREATED, reviewed.status(), body(reviewed));
        assertTrue(body(reviewed).contains("\"averageRating\":4.0"));
        var replied = web.post("/api/me/reviews/0/reply", driver, "reply=Merci+!");
        assertTrue(body(replied).contains("\"reply\":\"Merci !\""));
        assertEquals(HttpStatus.NOT_FOUND, web.post("/api/me/reviews/1/reply", driver, "reply=x").status());
    }

    @Test
    void emailIsVerifiedWithTheCodeSent() {
        var sent = body(web.post("/api/me/verifications/email", passenger, ""));
        var code = sent.replaceAll(".*\"demoCode\":\"(\\d+)\".*", "$1");
        var wrong = web.post("/api/me/verifications/email/confirm", passenger, "code=x");
        assertTrue(body(wrong).contains("INVALID_VERIFICATION_CODE"));
        var confirmed = web.post("/api/me/verifications/email/confirm", passenger, "code=" + code);
        assertTrue(body(confirmed).contains("\"email\":true"), body(confirmed));
    }

    @Test
    void ratingOutOfRangeIsADomainError() {
        var response = web.post("/api/users/" + driverId + "/reviews", passenger, "rating=6");
        assertEquals(HttpStatus.UNPROCESSABLE, response.status());
        assertTrue(body(response).contains("INVALID_RATING"));
    }

    @Test
    void unknownMemberIs404() {
        assertEquals(HttpStatus.NOT_FOUND, web.get("/api/users/999", passenger).status());
    }
}
