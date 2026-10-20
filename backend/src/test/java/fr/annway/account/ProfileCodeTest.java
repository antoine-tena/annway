package fr.annway.account;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ProfileCodeTest {

    private static final Instant NOW = Instant.parse("2026-10-20T08:00:00Z");
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    private final Profile profile = new Profile();

    @Test
    void rightCodeVerifiesThePhone() {
        var code = profile.requestPhoneCode(clock);
        profile.confirmPhone(code.code(), clock);
        assertTrue(profile.isPhoneVerified());
    }

    @Test
    void codeIsSingleUse() {
        var code = profile.requestEmailCode(clock);
        profile.confirmEmail(code.code(), clock);
        assertThrows(AccountException.class, () -> profile.confirmEmail(code.code(), clock));
    }

    @Test
    void expiredCodeIsRefused() {
        var code = profile.requestEmailCode(clock);
        var later = Clock.fixed(NOW.plus(VerificationCode.VALIDITY), ZoneOffset.UTC);
        assertThrows(AccountException.class, () -> profile.confirmEmail(code.code(), later));
    }

    @Test
    void noCodeRequestedIsRefused() {
        assertThrows(AccountException.class, () -> profile.confirmPhone("123456", clock));
    }
}
