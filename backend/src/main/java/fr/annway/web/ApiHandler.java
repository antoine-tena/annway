package fr.annway.web;

/** Traitement d'une route : reçoit l'appel déjà authentifié, délègue au domaine, répond. */
@FunctionalInterface
public interface ApiHandler {

    ApiResponse handle(ApiCall call);
}
