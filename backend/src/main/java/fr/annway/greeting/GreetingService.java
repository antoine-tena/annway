package fr.annway.greeting;

import java.util.Objects;

/** Example service: receives values, returns a value, never touches the terminal. */
public final class GreetingService {

    static final String DEFAULT_NAME = "monde";

    public String greet(String name) {
        Objects.requireNonNull(name, "name");
        var trimmedName = name.strip();
        return "Bonjour, " + (trimmedName.isEmpty() ? DEFAULT_NAME : trimmedName) + " !";
    }
}
