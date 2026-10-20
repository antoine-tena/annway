package fr.annway.web;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Objet JSON en construction, qui garde l'ordre des clés ; écrit par {@link Json}. */
public final class JsonObject {

    private final Map<String, Object> fields = new LinkedHashMap<>();

    /** Ajoute un champ : texte, nombre, booléen, {@code Optional}, liste ou autre objet JSON. */
    public JsonObject put(String key, Object value) {
        fields.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, key));
        return this;
    }

    public Map<String, Object> fields() {
        return Collections.unmodifiableMap(fields);
    }
}
