package fr.annway.account;

import java.util.Objects;
import java.util.Optional;

/**
 * Profil d'un utilisateur : présentation libre et vérifications de son identité. Un profil est
 * vérifié quand l'e-mail, le téléphone et la pièce d'identité l'ont tous été.
 */
public final class Profile {

    private String bio = "";
    private String photoUrl = "";
    private boolean isEmailVerified;
    private boolean isPhoneVerified;
    private boolean isIdentityDocumentVerified;

    public String bio() {
        return bio;
    }

    public void updateBio(String newBio) {
        bio = Objects.requireNonNull(newBio, "newBio").strip();
    }

    public Optional<String> photoUrl() {
        return photoUrl.isEmpty() ? Optional.empty() : Optional.of(photoUrl);
    }

    public void updatePhotoUrl(String newPhotoUrl) {
        photoUrl = Objects.requireNonNull(newPhotoUrl, "newPhotoUrl").strip();
    }

    public void verifyEmail() {
        isEmailVerified = true;
    }

    public void verifyPhone() {
        isPhoneVerified = true;
    }

    public void verifyIdentityDocument() {
        isIdentityDocumentVerified = true;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public boolean isPhoneVerified() {
        return isPhoneVerified;
    }

    public boolean isIdentityDocumentVerified() {
        return isIdentityDocumentVerified;
    }

    public boolean isVerified() {
        return isEmailVerified && isPhoneVerified && isIdentityDocumentVerified;
    }
}
