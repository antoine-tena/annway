package fr.annway.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.annway.trip.TripType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/** Écriture JSON et lecture des formulaires. */
class FormatsTest {

    @Test
    void jsonWritesEveryKindOfValue() {
        var object = new JsonObject()
                .put("text", "a\"b\\c\n\t\r<\u0001>")
                .put("count", 3)
                .put("cents", 1500L)
                .put("rating", 4.5)
                .put("isOpen", true)
                .put("type", TripType.STATION)
                .put("absent", Optional.empty())
                .put("list", List.of(1, Optional.of("x")));
        assertEquals("{\"text\":\"a\\\"b\\\\c\\n\\t\\r\\u003c\\u0001\\u003e\",\"count\":3,\"cents\":1500,"
                + "\"rating\":4.5,\"isOpen\":true,\"type\":\"STATION\",\"absent\":null,\"list\":[1,\"x\"]}",
                Json.write(object));
        assertThrows(IllegalArgumentException.class, () -> Json.write(new Object()));
    }

    @Test
    void formDecodesRepeatedAndEncodedFields() {
        var form = FormData.parse("name=L%C3%A9a+D&equipment=SKI:a&equipment=&equipment=SLED:b&flag&&empty=");
        assertEquals("Léa D", form.text("name"));
        assertEquals(List.of("SKI:a", "SLED:b"), form.all("equipment"));
        assertEquals(Optional.empty(), form.optionalText("flag"));
        assertEquals(Optional.empty(), form.optionalText("empty"));
        assertEquals(Optional.empty(), FormData.empty().optionalText("name"));
    }

    @Test
    void formReadsTypedFields() {
        var form = FormData.parse("seats=2&cents=1500&day=2026-12-16&at=07:30&when=2026-12-16T07:30&type=POLYTECH");
        assertEquals(2, form.integer("seats"));
        assertEquals(1500L, form.longValue("cents"));
        assertEquals(LocalDate.of(2026, 12, 16), form.date("day"));
        assertEquals(Optional.of(LocalTime.of(7, 30)), form.optionalTime("at"));
        assertEquals(LocalDate.of(2026, 12, 16), form.optionalDate("day").orElseThrow());
        assertEquals(TripType.POLYTECH, form.enumValue("type", TripType.class));
        assertEquals(Optional.empty(), form.optionalLong("missing"));
    }

    @Test
    void malformedFieldsAreInvalid() {
        var form = FormData.parse("seats=deux&day=16/12&type=CAR&long=" + "x".repeat(FormData.MAX_FIELD_LENGTH + 1));
        assertInvalid("seats", () -> form.integer("seats"));
        assertInvalid("day", () -> form.date("day"));
        assertInvalid("type", () -> form.enumValue("type", TripType.class));
        assertInvalid("long", () -> form.text("long"));
        assertInvalid("missing", () -> form.text("missing"));
        assertInvalid("%zz", () -> FormData.parse("%zz=1"));
    }

    private static void assertInvalid(String field, Executable read) {
        var error = assertThrows(WebException.class, read);
        assertEquals(WebError.INVALID_FIELD, error.error());
        assertEquals(Optional.of(field), error.field());
    }

    @Test
    void portIsReadFromTheEnvironment() {
        assertEquals(WebMain.DEFAULT_PORT, WebMain.port(Map.of()));
        assertEquals(9000, WebMain.port(Map.of("ANNWAY_PORT", "9000")));
        assertThrows(IllegalArgumentException.class, () -> WebMain.port(Map.of("ANNWAY_PORT", "0")));
        assertThrows(IllegalArgumentException.class, () -> WebMain.port(Map.of("ANNWAY_PORT", "http")));
        assertEquals(Optional.empty(), WebMain.demoPassword(Map.of("ANNWAY_DEMO_PASSWORD", " ")));
    }
}
