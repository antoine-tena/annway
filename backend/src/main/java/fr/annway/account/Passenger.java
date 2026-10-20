package fr.annway.account;

import fr.annway.equipment.Equipment;
import fr.annway.search.TripAlert;
import fr.annway.search.TripCatalogue;
import fr.annway.trip.Reservation;
import fr.annway.trip.SearchCriteria;
import fr.annway.trip.Trip;
import fr.annway.trip.TripRequest;
import java.time.Clock;
import java.util.List;
import java.util.Objects;

/**
 * Passager : cherche des trajets dans le catalogue, les réserve, s'abonne à des alertes ou fait
 * une demande quand rien ne lui convient. Chaque action délègue aux trajets et au catalogue.
 */
public final class Passenger extends User {

    public Passenger(int id, String firstName, String lastName, String email, String phone) {
        super(id, firstName, lastName, email, phone);
    }

    /** Trajets du catalogue qui répondent aux critères, passés au filtre du catalogue. */
    public List<Trip> searchTrip(SearchCriteria criteria, TripCatalogue catalogue) {
        return Objects.requireNonNull(catalogue, "catalogue").search(criteria);
    }

    /** Demande des places sur un trajet, avec le matériel déclaré ; le conducteur la valide. */
    public Reservation book(int reservationId, Trip trip, int seatCount, List<Equipment> equipment, Clock clock) {
        return Reservation.request(reservationId, trip, this, seatCount, equipment, clock);
    }

    /** Demande de trajet datée du jour, quand la recherche n'a rien donné. */
    public TripRequest createTripRequest(SearchCriteria criteria, Clock clock) {
        return TripRequest.create(this, criteria, clock);
    }

    /** Abonne au catalogue une alerte sur ces critères ; elle garde les trajets publiés ensuite. */
    public TripAlert subscribeAlert(SearchCriteria criteria, TripCatalogue catalogue) {
        Objects.requireNonNull(catalogue, "catalogue");
        var alert = new TripAlert(this, criteria);
        catalogue.subscribe(alert);
        return alert;
    }
}
