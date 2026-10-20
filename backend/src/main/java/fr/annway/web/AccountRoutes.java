package fr.annway.web;

import java.time.Clock;
import java.util.List;
import java.util.Objects;

/** Compte connecté, profils publics, véhicules et avis. */
public final class AccountRoutes {

    private final AnnwayStore store;
    private final Clock clock;

    public AccountRoutes(AnnwayStore store, Clock clock) {
        this.store = Objects.requireNonNull(store, "store");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public List<Route> routes() {
        return List.of(
                Route.get("/api/me", Access.MEMBER, call -> ApiResponse.ok(AccountViews.me(call.member()))),
                Route.post("/api/me/profile", Access.MEMBER, this::updateProfile),
                Route.post("/api/me/verifications/email", Access.MEMBER, this::requestEmailCode),
                Route.post("/api/me/verifications/email/confirm", Access.MEMBER, this::confirmEmail),
                Route.post("/api/me/verifications/phone", Access.MEMBER, this::requestPhoneCode),
                Route.post("/api/me/verifications/phone/confirm", Access.MEMBER, this::confirmPhone),
                Route.post("/api/me/vehicles", Access.DRIVER, this::addVehicle),
                Route.post("/api/me/reviews/{index}/reply", Access.MEMBER, this::reply),
                Route.get("/api/users/{id}", Access.MEMBER, this::publicProfile),
                Route.post("/api/users/{id}/reviews", Access.MEMBER, this::leaveReview));
    }

    private ApiResponse updateProfile(ApiCall call) {
        var member = call.member();
        member.profile().updateBio(call.form().optionalText("bio").orElse(""));
        return ApiResponse.ok(AccountViews.me(member));
    }

    /** Aucun envoi d'e-mail ni de SMS n'est branché : le code revient dans la réponse, pour la démo. */
    private ApiResponse requestEmailCode(ApiCall call) {
        var code = call.member().profile().requestEmailCode(clock);
        return ApiResponse.created(new JsonObject().put("demoCode", code.code()));
    }

    private ApiResponse confirmEmail(ApiCall call) {
        var member = call.member();
        member.profile().confirmEmail(call.form().text("code"), clock);
        return ApiResponse.ok(AccountViews.me(member));
    }

    private ApiResponse requestPhoneCode(ApiCall call) {
        var code = call.member().profile().requestPhoneCode(clock);
        return ApiResponse.created(new JsonObject().put("demoCode", code.code()));
    }

    private ApiResponse confirmPhone(ApiCall call) {
        var member = call.member();
        member.profile().confirmPhone(call.form().text("code"), clock);
        return ApiResponse.ok(AccountViews.me(member));
    }

    private ApiResponse addVehicle(ApiCall call) {
        var driver = call.driver();
        driver.addVehicle(AuthRoutes.vehicleOf(call.form()));
        return ApiResponse.created(AccountViews.me(driver));
    }

    private ApiResponse publicProfile(ApiCall call) {
        return ApiResponse.ok(AccountViews.publicProfile(store.requireUser(call.pathId("id"))));
    }

    private ApiResponse leaveReview(ApiCall call) {
        var target = store.requireUser(call.pathId("id"));
        var form = call.form();
        call.member().leaveReview(target, form.integer("rating"), form.optionalText("comment").orElse(""), clock);
        return ApiResponse.created(AccountViews.publicProfile(target));
    }

    private ApiResponse reply(ApiCall call) {
        var member = call.member();
        var reviews = member.receivedReviews();
        var index = call.pathId("index");
        if (index < 0 || index >= reviews.size()) {
            throw new WebException(WebError.NOT_FOUND);
        }
        reviews.get(index).reply(member, call.form().text("reply"));
        return ApiResponse.ok(AccountViews.me(member));
    }
}
