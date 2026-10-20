package fr.annway.account;

import java.time.Clock;
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
    private VerificationCode pendingEmailCode;
    private VerificationCode pendingPhoneCode;

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

    /** Émet un nouveau code pour l'e-mail ; il remplace le précédent. */
    public VerificationCode requestEmailCode(Clock clock) {
        pendingEmailCode = VerificationCode.issue(clock);
        return pendingEmailCode;
    }

    public void confirmEmail(String code, Clock clock) {
        requireValid(pendingEmailCode, code, clock);
        pendingEmailCode = null;
        verifyEmail();
    }

    /** Émet un nouveau code pour le téléphone ; il remplace le précédent. */
    public VerificationCode requestPhoneCode(Clock clock) {
        pendingPhoneCode = VerificationCode.issue(clock);
        return pendingPhoneCode;
    }

    public void confirmPhone(String code, Clock clock) {
        requireValid(pendingPhoneCode, code, clock);
        pendingPhoneCode = null;
        verifyPhone();
    }

    private static void requireValid(VerificationCode pending, String code, Clock clock) {
        if (pending == null || !pending.matches(code, clock)) {
            throw new AccountException(AccountError.INVALID_VERIFICATION_CODE);
        }
    }

    /** La pièce d'identité est contrôlée à la main par un organisateur de la sortie. */
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
