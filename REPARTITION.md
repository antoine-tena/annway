# Répartition des modules

Chaque classe a **un seul propriétaire**. Personne, humain ou agent, ne crée, ne modifie ni ne
supprime une classe d'un autre module. Si un changement est nécessaire chez quelqu'un d'autre,
on le lui demande (issue ou message) et c'est lui qui le fait.

Un agent (Claude Code ou autre) qui travaille pour un membre de l'équipe ne touche qu'aux classes
de ce membre. Une demande qui sort de son module est refusée, et l'agent indique à qui s'adresser.

## Partie 1 : Identité, comptes et social (Pavlo)

Hiérarchie des comptes, sécurité des profils, véhicules du conducteur, avis et messagerie.

- `Utilisateur` (abstraite), `Passager`, `Conducteur`
- `Profil` (vérification de l'identité, de l'e-mail et du téléphone)
- `Vehicule` (modèle, immatriculation, capacité)
- `Avis` (notes et commentaires croisés)
- `Message` (messagerie liée aux annonces)
- Erreurs métier du domaine : `AccountError` (énumération), `AccountException`

Dans le code (paquet `fr.annway.account`, en anglais) : `User`, `Passenger`, `Driver`,
`Profile`, `Vehicle`, `Review`, `Message`. Données de test : `AccountFixtures`.

## Partie 2 : Trajets, réservations et paiement (Antoine)

Polymorphisme des trajets (station à prix fixe, Polytech à don libre), cycle de vie des
réservations, paiement tiers via l'adaptateur.

- `Trajet` (abstraite), `TrajetStation`, `TrajetPolytech`, `TypeTrajet` (énumération)
- `Etape`, `StatutTrajet` (énumération)
- `DemandeTrajet`, `CritereRecherche`
- `Reservation`, `StatutReservation` (énumération)
- `Paiement`, `TypePaiement` (`PRIX_FIXE` ou `DON`)
- Adaptateur : `ServicePaiement` (interface), `AdaptateurPaiementExterne`, `ApiPaiementExterne`

Dans le code (paquet `fr.annway.trip`, en anglais) : `Trip`, `StationTrip`, `PolytechTrip`,
`TripType`, `TripRoute` (identifiant, départ, arrivée, instant de départ), `Stop`, `TripStatus`,
`SearchCriteria`, `TripRequest`, `Reservation`, `ReservationStatus`, `Payment`, `PaymentType`.
Erreurs métier : `TripError` (énumération), `TripException`. Données de test : `TripFixtures`.
Adaptateur (paquet `fr.annway.payment`) : `PaymentService`, `ExternalPaymentAdapter`,
`ExternalPaymentApi`.

## Partie 3 : Catalogue, recherche et patrons avancés (Nathan)

Fabrique de matériel, catalogue et alertes de trajets, filtres de recherche interchangeables.

- Simple Factory : `Materiel` (abstraite), `Ski`, `Snowboard`, `Luge`, `TypeMateriel`,
  `MaterielFactory`
- Observateur : `SujetTrajets` (interface), `ObservateurTrajet` (interface),
  `CatalogueTrajets`, `AlerteTrajet`
- Stratégie : `StrategieFiltre` (interface), `FiltreParPrix`, `FiltreParHoraire`, `SansFiltre`

Dans le code (paquet `fr.annway.search`, en anglais) : `TripFilter`, `PriceFilter`,
`TimeWindowFilter`, `NoFilter` ; `TripSubject`, `TripObserver`, `TripCatalogue`, `TripAlert`. Erreurs métier : `SearchError` (énumération), `SearchException`.
Données de test : `SearchFixtures`.

## Points de contact entre modules

Un module **utilise** les classes d'un autre sans les modifier :

- `Reservation` (Antoine) fait appel à `Materiel` (Nathan).
- `Trajet` (Antoine) est rattaché à un `Conducteur` et à son `Vehicule` (Pavlo).
- `CatalogueTrajets` et les filtres (Nathan) travaillent sur des `Trajet` (Antoine).

Ce qui ne figure dans aucune liste (classe nouvelle, utilitaire partagé) se décide en équipe
avant d'être écrit, puis s'ajoute ici à un propriétaire.
