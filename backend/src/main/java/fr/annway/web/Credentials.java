package fr.annway.web;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntSupplier;

/** Identifiants de connexion : l'e-mail (en minuscules) mène au compte et à son empreinte. */
public final class Credentials {

    private record Account(int userId, PasswordHash passwordHash) {
    }

    private final PasswordHasher hasher;
    private final Map<String, Account> accountsByEmail = new HashMap<>();

    public Credentials(PasswordHasher hasher) {
        this.hasher = Objects.requireNonNull(hasher, "hasher");
    }

    public boolean isTaken(String email) {
        return accountsByEmail.containsKey(normalize(email));
    }

    /**
     * Inscrit un compte : le mot de passe est haché (trop court, refusé) et l'e-mail vérifié
     * libre (sinon 409) avant que {@code createAccount} ne crée le compte et rende son identifiant.
     */
    public int register(String email, String password, IntSupplier createAccount) {
        var key = normalize(email);
        var passwordHash = hasher.hash(password);
        if (accountsByEmail.containsKey(key)) {
            throw new WebException(WebError.EMAIL_TAKEN, "email");
        }
        var userId = createAccount.getAsInt();
        accountsByEmail.put(key, new Account(userId, passwordHash));
        return userId;
    }

    /** Identifiant du compte si l'e-mail et le mot de passe correspondent. */
    public Optional<Integer> authenticate(String email, String password) {
        var account = accountsByEmail.get(normalize(email));
        if (account == null || !hasher.matches(password, account.passwordHash())) {
            return Optional.empty();
        }
        return Optional.of(account.userId());
    }

    private static String normalize(String email) {
        return Objects.requireNonNull(email, "email").strip().toLowerCase(Locale.ROOT);
    }
}
