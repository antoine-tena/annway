package fr.annway.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Entrée HTTP ([LAYERS]) : traduit l'échange du serveur du JDK en {@link ApiRequest}, confie la
 * requête au routeur et écrit la réponse JSON avec les en-têtes de sécurité.
 */
public final class HttpEntry implements HttpHandler {

    static final int MAX_BODY_BYTES = 16 * 1024;
    private static final int NO_BODY = -1;
    private static final String FORM_TYPE = "application/x-www-form-urlencoded";

    /** En-têtes de sécurité de chaque réponse ([SECURITY-HEADERS]) ; l'API ne sert que du JSON. */
    public static final Map<String, String> SECURITY_HEADERS = Map.of(
            "Strict-Transport-Security", "max-age=63072000; includeSubDomains; preload",
            "X-Content-Type-Options", "nosniff",
            "Referrer-Policy", "strict-origin-when-cross-origin",
            "Permissions-Policy", "camera=(), microphone=(), geolocation=()",
            "Cross-Origin-Opener-Policy", "same-origin",
            "Content-Security-Policy", "default-src 'none'; frame-ancestors 'none'",
            "Cache-Control", "no-store");

    private final Router router;

    public HttpEntry(Router router) {
        this.router = Objects.requireNonNull(router, "router");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            ApiResponse response;
            try {
                response = router.handle(toRequest(exchange));
            } catch (WebException rejected) {
                response = ApiResponse.error(rejected.error().status(), rejected.code(),
                        rejected.getMessage(), rejected.field());
            }
            write(exchange, response);
        }
    }

    private static ApiRequest toRequest(HttpExchange exchange) throws IOException {
        var uri = exchange.getRequestURI();
        var query = Optional.ofNullable(uri.getRawQuery()).map(FormData::parse).orElse(FormData.empty());
        var headers = exchange.getRequestHeaders();
        var token = Optional.ofNullable(headers.getFirst("Cookie")).flatMap(SessionCookie::read);
        var clientKey = ClientKey.of(exchange.getRemoteAddress().getAddress());
        return new ApiRequest(exchange.getRequestMethod(), uri.getPath(), query, readForm(exchange), token, clientKey);
    }

    /** Corps de formulaire ; un autre type de contenu est ignoré, un corps trop gros refusé (413). */
    private static FormData readForm(HttpExchange exchange) throws IOException {
        var contentType = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Content-Type")).orElse("");
        if (!contentType.startsWith(FORM_TYPE)) {
            return FormData.empty();
        }
        var bytes = exchange.getRequestBody().readNBytes(MAX_BODY_BYTES + 1);
        if (bytes.length > MAX_BODY_BYTES) {
            throw new WebException(WebError.BODY_TOO_LARGE);
        }
        return FormData.parse(new String(bytes, StandardCharsets.UTF_8));
    }

    private static void write(HttpExchange exchange, ApiResponse response) throws IOException {
        var headers = exchange.getResponseHeaders();
        SECURITY_HEADERS.forEach(headers::set);
        response.cookies().forEach(cookie -> headers.add("Set-Cookie", cookie));
        if (response.body().isEmpty()) {
            exchange.sendResponseHeaders(response.status(), NO_BODY);
            return;
        }
        var bytes = response.body().orElseThrow().getBytes(StandardCharsets.UTF_8);
        headers.set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(response.status(), bytes.length);
        exchange.getResponseBody().write(bytes);
    }
}
