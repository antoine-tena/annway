package fr.annway.account;

/**
 * Passager : cherche et réserve des trajets. La recherche, la réservation, les demandes et les
 * alertes s'ajoutent quand les classes des trajets et du catalogue existent.
 */
public final class Passenger extends User {

    public Passenger(int id, String firstName, String lastName, String email, String phone) {
        super(id, firstName, lastName, email, phone);
    }
}
