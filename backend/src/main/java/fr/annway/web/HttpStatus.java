package fr.annway.web;

/** Statuts HTTP que l'API renvoie ([NO-MAGIC-VALUES]). */
public final class HttpStatus {

    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int NO_CONTENT = 204;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int CONFLICT = 409;
    public static final int PAYLOAD_TOO_LARGE = 413;
    /** Requête bien formée que le domaine refuse (plus de place, déjà payée…). */
    public static final int UNPROCESSABLE = 422;
    public static final int TOO_MANY_REQUESTS = 429;
    public static final int INTERNAL_ERROR = 500;

    private HttpStatus() {
    }
}
