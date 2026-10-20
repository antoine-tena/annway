package fr.annway.web;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Champs d'une chaîne de requête ou d'un corps {@code application/x-www-form-urlencoded}. Chaque
 * lecture valide le champ à la frontière ([VALIDATE-AT-BOUNDARIES]) : un champ absent, vide,
 * trop long ou mal formé lève {@link WebError#INVALID_FIELD} avec son nom.
 */
public final class FormData {

    static final int MAX_FIELD_LENGTH = 2000;

    private final Map<String, List<String>> fields;

    private FormData(Map<String, List<String>> fields) {
        this.fields = fields;
    }

    public static FormData empty() {
        return new FormData(Map.of());
    }

    /** Découpe {@code a=1&b=deux} ; une clé répétée garde toutes ses valeurs, dans l'ordre. */
    public static FormData parse(String encoded) {
        Objects.requireNonNull(encoded, "encoded");
        var fields = new LinkedHashMap<String, List<String>>();
        for (var pair : encoded.split("&")) {
            if (pair.isEmpty()) {
                continue;
            }
            var separator = pair.indexOf('=');
            var key = decode(separator < 0 ? pair : pair.substring(0, separator));
            var value = separator < 0 ? "" : decode(pair.substring(separator + 1));
            fields.computeIfAbsent(key, ignored -> new ArrayList<>()).add(value);
        }
        return new FormData(fields);
    }

    private static String decode(String text) {
        try {
            return URLDecoder.decode(text, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException malformed) {
            throw WebException.invalidField(text);
        }
    }

    public Optional<String> optionalText(String name) {
        var values = fields.getOrDefault(name, List.of());
        if (values.isEmpty() || values.getFirst().isBlank()) {
            return Optional.empty();
        }
        return Optional.of(checkLength(name, values.getFirst().strip()));
    }

    public String text(String name) {
        return optionalText(name).orElseThrow(() -> WebException.invalidField(name));
    }

    /** Toutes les valeurs d'un champ répété, vides écartées. */
    public List<String> all(String name) {
        return fields.getOrDefault(name, List.of()).stream()
                .filter(value -> !value.isBlank())
                .map(value -> checkLength(name, value.strip()))
                .toList();
    }

    public Optional<Integer> optionalInteger(String name) {
        return optionalText(name).map(text -> parseNumber(name, text, Integer::parseInt));
    }

    public int integer(String name) {
        return parseNumber(name, text(name), Integer::parseInt);
    }

    public Optional<Long> optionalLong(String name) {
        return optionalText(name).map(text -> parseNumber(name, text, Long::parseLong));
    }

    public long longValue(String name) {
        return parseNumber(name, text(name), Long::parseLong);
    }

    public Optional<LocalDate> optionalDate(String name) {
        return optionalText(name).map(text -> parseTemporal(name, text, LocalDate::parse));
    }

    public LocalDate date(String name) {
        return parseTemporal(name, text(name), LocalDate::parse);
    }

    public Optional<LocalTime> optionalTime(String name) {
        return optionalText(name).map(text -> parseTemporal(name, text, LocalTime::parse));
    }

    /** Date et heure locales, au format {@code 2026-12-12T07:30}. */
    public LocalDateTime dateTime(String name) {
        return parseTemporal(name, text(name), LocalDateTime::parse);
    }

    /** Valeur d'une énumération, par son nom exact ({@code STATION}). */
    public <E extends Enum<E>> Optional<E> optionalEnum(String name, Class<E> type) {
        return optionalText(name).map(text -> parseEnum(name, text, type));
    }

    public <E extends Enum<E>> E enumValue(String name, Class<E> type) {
        return parseEnum(name, text(name), type);
    }

    private static <E extends Enum<E>> E parseEnum(String name, String text, Class<E> type) {
        try {
            return Enum.valueOf(type, text);
        } catch (IllegalArgumentException unknown) {
            throw WebException.invalidField(name);
        }
    }

    private static String checkLength(String name, String value) {
        if (value.length() > MAX_FIELD_LENGTH) {
            throw WebException.invalidField(name);
        }
        return value;
    }

    private interface Parser<T> {
        T parse(String text);
    }

    private static <T> T parseNumber(String name, String text, Parser<T> parser) {
        try {
            return parser.parse(text);
        } catch (NumberFormatException malformed) {
            throw WebException.invalidField(name);
        }
    }

    private static <T> T parseTemporal(String name, String text, Parser<T> parser) {
        try {
            return parser.parse(text);
        } catch (DateTimeParseException malformed) {
            throw WebException.invalidField(name);
        }
    }
}
