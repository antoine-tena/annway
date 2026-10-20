package fr.annway.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Réponse de l'API : statut, corps JSON (absent pour 204) et cookies à poser. */
public record ApiResponse(int status, Optional<String> body, List<String> cookies) {

    public ApiResponse {
        Objects.requireNonNull(body, "body");
        cookies = List.copyOf(cookies);
    }

    public static ApiResponse ok(Object payload) {
        return new ApiResponse(HttpStatus.OK, Optional.of(Json.write(payload)), List.of());
    }

    public static ApiResponse created(Object payload) {
        return new ApiResponse(HttpStatus.CREATED, Optional.of(Json.write(payload)), List.of());
    }

    public static ApiResponse noContent() {
        return new ApiResponse(HttpStatus.NO_CONTENT, Optional.empty(), List.of());
    }

    /** Erreur au format commun : {@code {"code": "...", "message": "...", "field": ...}}. */
    public static ApiResponse error(int status, String code, String message, Optional<String> field) {
        var payload = new JsonObject().put("code", code).put("message", message).put("field", field);
        return new ApiResponse(status, Optional.of(Json.write(payload)), List.of());
    }

    public ApiResponse withCookie(String cookie) {
        var allCookies = new ArrayList<>(cookies);
        allCookies.add(Objects.requireNonNull(cookie, "cookie"));
        return new ApiResponse(status, body, allCookies);
    }
}
