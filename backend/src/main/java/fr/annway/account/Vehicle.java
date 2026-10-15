package fr.annway.account;

import java.util.Locale;

/**
 * Véhicule d'un conducteur : places assises pour les passagers, capacité pour leur matériel
 * (en unités d'encombrement). L'immatriculation est normalisée en majuscules.
 */
public record Vehicle(String registration, String model, int seatCount, int equipmentCapacity) {

    static final int MIN_SEAT_COUNT = 1;

    public Vehicle {
        registration = AccountException.requireNotBlank(registration, "registration").toUpperCase(Locale.ROOT);
        model = AccountException.requireNotBlank(model, "model");
        if (seatCount < MIN_SEAT_COUNT) {
            throw new AccountException(AccountError.INVALID_SEAT_COUNT);
        }
        if (equipmentCapacity < 0) {
            throw new AccountException(AccountError.INVALID_EQUIPMENT_CAPACITY);
        }
    }
}
