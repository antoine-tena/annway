package fr.annway.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProfileTest {

    private final Profile profile = new Profile();

    @Test
    void newProfileIsNotVerified() {
        assertFalse(profile.isEmailVerified());
        assertFalse(profile.isPhoneVerified());
        assertFalse(profile.isIdentityDocumentVerified());
        assertFalse(profile.isVerified());
    }

    @Test
    void partialVerificationIsNotEnough() {
        profile.verifyEmail();
        profile.verifyPhone();
        assertTrue(profile.isEmailVerified());
        assertTrue(profile.isPhoneVerified());
        assertFalse(profile.isVerified());
    }

    @Test
    void allThreeVerificationsMakeProfileVerified() {
        profile.verifyEmail();
        profile.verifyPhone();
        profile.verifyIdentityDocument();
        assertTrue(profile.isIdentityDocumentVerified());
        assertTrue(profile.isVerified());
    }

    @Test
    void bioAndPhotoCanBeUpdated() {
        assertEquals("", profile.bio());
        assertEquals(Optional.empty(), profile.photoUrl());
        profile.updateBio("  Skieur du dimanche  ");
        profile.updatePhotoUrl("https://example.org/photo.jpg");
        assertEquals("Skieur du dimanche", profile.bio());
        assertEquals(Optional.of("https://example.org/photo.jpg"), profile.photoUrl());
    }
}
