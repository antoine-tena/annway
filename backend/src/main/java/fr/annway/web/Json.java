package fr.annway.web;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Écrit les réponses de l'API en JSON, sans bibliothèque ([NEW-DEPENDENCY]) : objets
 * {@link JsonObject}, listes, textes, nombres, booléens ; un {@code Optional} vide devient
 * {@code null}.
 */
public final class Json {

    private static final int LAST_CONTROL_CHARACTER = 0x1f;

    private Json() {
    }

    public static String write(Object value) {
        var out = new StringBuilder();
        append(out, value);
        return out.toString();
    }

    private static void append(StringBuilder out, Object value) {
        switch (value) {
            case null -> out.append("null");
            case String text -> appendString(out, text);
            case Integer number -> out.append(number);
            case Long number -> out.append(number);
            case Double number -> out.append(number);
            case Boolean flag -> out.append(flag);
            case Enum<?> constant -> appendString(out, constant.name());
            case Optional<?> optional -> append(out, optional.orElse(null));
            case JsonObject object -> appendObject(out, object.fields());
            case Collection<?> list -> appendArray(out, list);
            default -> throw new IllegalArgumentException(
                    "type non sérialisable : " + value.getClass().getSimpleName());
        }
    }

    private static void appendObject(StringBuilder out, Map<String, Object> fields) {
        out.append('{');
        var isFirst = true;
        for (var entry : fields.entrySet()) {
            if (!isFirst) {
                out.append(',');
            }
            isFirst = false;
            appendString(out, entry.getKey());
            out.append(':');
            append(out, entry.getValue());
        }
        out.append('}');
    }

    private static void appendArray(StringBuilder out, Collection<?> list) {
        out.append('[');
        var isFirst = true;
        for (var element : list) {
            if (!isFirst) {
                out.append(',');
            }
            isFirst = false;
            append(out, element);
        }
        out.append(']');
    }

    private static void appendString(StringBuilder out, String text) {
        out.append('"');
        text.chars().forEach(character -> appendCharacter(out, (char) character));
        out.append('"');
    }

    private static void appendCharacter(StringBuilder out, char character) {
        switch (character) {
            case '"' -> out.append("\\\"");
            case '\\' -> out.append("\\\\");
            case '\n' -> out.append("\\n");
            case '\r' -> out.append("\\r");
            case '\t' -> out.append("\\t");
            // Chevrons échappés : un texte d'utilisateur ne ferme jamais une balise <script>.
            case '<' -> out.append("\\u003c");
            case '>' -> out.append("\\u003e");
            default -> {
                if (character <= LAST_CONTROL_CHARACTER) {
                    out.append(String.format("\\u%04x", (int) character));
                } else {
                    out.append(character);
                }
            }
        }
    }
}
