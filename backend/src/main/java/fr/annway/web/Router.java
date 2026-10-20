package fr.annway.web;

import fr.annway.account.AccountException;
import fr.annway.equipment.EquipmentException;
import fr.annway.search.SearchException;
import fr.annway.trip.TripException;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Aiguille une requête vers sa route ([LAYERS]) : chemin inconnu 404, méthode refusée 405,
 * limite de débit sur les routes publiques, authentification déclarée par la route (401, 403).
 * Les erreurs du domaine deviennent des réponses 422 avec leur code et leur message.
 */
public final class Router {

    private static final System.Logger LOG = System.getLogger(Router.class.getName());

    private final List<Route> routes;
    private final SessionStore sessions;
    private final AnnwayStore store;
    private final RateLimiter publicLimiter;

    public Router(List<Route> routes, SessionStore sessions, AnnwayStore store, RateLimiter publicLimiter) {
        this.routes = List.copyOf(routes);
        this.sessions = Objects.requireNonNull(sessions, "sessions");
        this.store = Objects.requireNonNull(store, "store");
        this.publicLimiter = Objects.requireNonNull(publicLimiter, "publicLimiter");
    }

    public List<Route> routes() {
        return routes;
    }

    public ApiResponse handle(ApiRequest request) {
        try {
            return dispatch(request);
        } catch (WebException error) {
            return ApiResponse.error(error.error().status(), error.code(), error.getMessage(), error.field());
        } catch (AccountException error) {
            return domainError(error.code(), error.getMessage());
        } catch (TripException error) {
            return domainError(error.code(), error.getMessage());
        } catch (SearchException error) {
            return domainError(error.code(), error.getMessage());
        } catch (EquipmentException error) {
            return domainError(error.code(), error.getMessage());
        } catch (RuntimeException unexpected) {
            LOG.log(Level.ERROR, "unexpected error method={0} path={1}", request.method(), request.path());
            LOG.log(Level.ERROR, "cause of the unexpected error", unexpected);
            var internal = WebError.INTERNAL;
            return ApiResponse.error(internal.status(), internal.name(), internal.userMessage(), Optional.empty());
        }
    }

    private ApiResponse dispatch(ApiRequest request) {
        var pathMatches = routes.stream()
                .map(route -> Map.entry(route, route.matchPath(request.path())))
                .filter(match -> match.getValue().isPresent())
                .toList();
        if (pathMatches.isEmpty()) {
            throw new WebException(WebError.NOT_FOUND);
        }
        var match = pathMatches.stream()
                .filter(candidate -> candidate.getKey().method().equals(request.method()))
                .findFirst()
                .orElseThrow(() -> new WebException(WebError.METHOD_NOT_ALLOWED));
        var route = match.getKey();
        if (route.access() == Access.PUBLIC) {
            publicLimiter.acquire(request.clientKey());
        }
        var user = request.sessionToken().flatMap(sessions::find).flatMap(store::user);
        route.access().check(user);
        return route.handler().handle(new ApiCall(request, match.getValue().orElseThrow(), user));
    }

    private static ApiResponse domainError(String code, String message) {
        return ApiResponse.error(HttpStatus.UNPROCESSABLE, code, message, Optional.empty());
    }
}
