package fr.annway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void greetingAnnouncesStartup() {
        assertEquals("annway démarré", Main.greeting());
    }
}
