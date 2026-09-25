package fr.annway;

/** Point d'entrée d'annway. */
public final class Main {

    private static final System.Logger LOG = System.getLogger(Main.class.getName());

    private Main() {
    }

    public static void main(String[] args) {
        LOG.log(System.Logger.Level.INFO, greeting());
    }

    static String greeting() {
        return "annway démarré";
    }
}
