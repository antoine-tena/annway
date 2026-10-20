package fr.annway.web;

import java.util.Arrays;
import java.util.Objects;

/** Empreinte d'un mot de passe et son sel ; les tableaux sont copiés, jamais partagés. */
public record PasswordHash(byte[] salt, byte[] hash) {

    public PasswordHash {
        salt = Objects.requireNonNull(salt, "salt").clone();
        hash = Objects.requireNonNull(hash, "hash").clone();
    }

    @Override
    public byte[] salt() {
        return salt.clone();
    }

    @Override
    public byte[] hash() {
        return hash.clone();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PasswordHash that
                && Arrays.equals(salt, that.salt)
                && Arrays.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(salt), Arrays.hashCode(hash));
    }

    /** N'expose ni le sel ni l'empreinte. */
    @Override
    public String toString() {
        return "PasswordHash[…]";
    }
}
