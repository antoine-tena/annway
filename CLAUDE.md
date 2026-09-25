# annway

Covoiturage pour les sorties annski : proposer un trajet vers la station, réserver une place.

Ce dépôt est **indépendant** d'annski : il ne dépend d'aucun de ses fichiers et ne doit pas
supposer qu'annski est présent sur la machine.

## Stack

- **Tout le projet est en Java** : Java 25 (LTS), aucun autre langage applicatif.
- **Aucun framework.** Le dépôt part vide : l'outil de build, la façon de tester et toute
  bibliothèque se décident en équipe, puis se décrivent ici (avec les commandes pour lancer,
  tester et vérifier le code) et se branchent dans la CI.
- Sources dans `src/main/java`, tests dans `src/test/java`, paquet racine `fr.annway`, un paquet
  par domaine (`fr.annway.trips`, `fr.annway.bookings`…).
- CI : `.github/workflows/ci.yml` (secrets, compilation). Déploiement :
  `.github/workflows/deploy.yml`, lancé à la main depuis `main`.

## Langue

- Échanges, documentation et messages de commit en français ; code en anglais.

## Règles de code

Une règle se cite par son identifiant entre crochets, jamais par un numéro de ligne. Une règle
impossible à tenir s'amende ici, avec sa raison ; on ne la contourne pas en silence.

En cas de conflit : **sécurité et intégrité des données > contrat d'API publié > lisibilité >
règles de forme**.

### Taille et découpage
- **[TAILLE-FICHIER]** 300 lignes au plus par fichier `.java`.
- **[TAILLE-METHODE]** Une méthode = une responsabilité, 30 lignes au plus. Un test peut aller
  jusqu'à 40 lignes.
- **[UNE-CLASSE]** Une classe publique par fichier (imposé par Java) ; pas de classe interne qui
  mériterait son propre fichier.
- **[COUCHES]** Entrée (HTTP, ligne de commande) → service → stockage. L'entrée reçoit, vérifie
  l'accès, délègue et répond ; aucune logique métier dans l'entrée, aucun objet HTTP dans un
  service.

### Nommage
- **[NOMS-INTERDITS]** Jamais de noms génériques : `data`, `res`, `val`, `tmp`, `info`, `obj`,
  `item` seul, `result` seul, `x`. Le nom dit ce que contient la variable (`seatCount`,
  `tripPriceCents`).
- **[VOCABULAIRE]** Le code est en anglais (`Trip`, `Ride`, `Seat`, `Driver`), les textes affichés
  en français.
- **[CASSE]** Classes en `PascalCase`, méthodes et variables en `camelCase`, constantes en
  `UPPER_SNAKE_CASE`, paquets en minuscules (`fr.annway.trips`).
- **[BOOLEENS]** Un booléen est préfixé : `is`, `has`, `should`, `can` (`isFull`, `canBook`).
- **[ZERO-MAGIE]** Pas de littéral métier en dur (statut, rôle, seuil, délai, prix) : `enum` ou
  constante `static final` nommée. `0`, `1`, `-1`, `""` et les littéraux des tests ne sont pas
  magiques.

### Java
- **[JAVA-MODERNE]** `record` pour les objets de données immuables (DTO, valeurs), `sealed` +
  `switch` par motifs pour les aiguillages sur un statut, `var` seulement quand le type se lit
  à droite.
- **[NULL]** Une méthode ne renvoie jamais `null` : `Optional<T>` pour une valeur absente,
  collection vide pour une liste. Paramètres vérifiés avec `Objects.requireNonNull` aux
  frontières.
- **[TYPES-BRUTS]** Pas de types bruts (`List` sans paramètre), pas de cast non vérifié sans
  justification en commentaire.
- **[EXCEPTIONS]** Jamais de `catch` vide ni de `catch (Exception e)` générique qui avale
  l'erreur. Les erreurs métier sont des exceptions dédiées, avec un code machine en
  `UPPER_SNAKE_CASE` et un message en français pour l'utilisateur.
- **[JOURNALISATION]** Jamais de `System.out` / `printStackTrace` : le logger du JDK
  (`System.getLogger`), avec des messages paramétrés
  (`LOG.log(Level.INFO, "trip booked tripId={0}", tripId)`), pas de concaténation.

### Argent, dates, données personnelles
- **[ARGENT]** Tout montant est un entier en centimes (`long priceCents`), dans la base, l'API et
  l'affichage. Jamais `double` ni `float` ; conversion en euros uniquement à l'affichage.
- **[FUSEAU]** Instants stockés en UTC (`Instant`) ; toute notion calendaire (« aujourd'hui »,
  date de départ) se calcule en `ZoneId.of("Europe/Paris")`. L'horloge est injectée (`Clock`),
  jamais `LocalDate.now()` sans argument.
- **[DONNEES-PERSO]** Aucune donnée personnelle (e-mail, nom, téléphone, adresse, IP complète)
  dans les logs, les messages d'erreur, les URL ou les clés de cache : on journalise
  l'identifiant, pas la personne.
- **[SECRETS]** Aucun secret dans le code ni dans le dépôt : tout passe par l'environnement.
  Un secret commis par erreur est considéré compromis et changé.

### Sécurité et données
- **[ENTREES]** Toute donnée qui entre (requête, API tierce, paramètre d'URL) est validée
  avant usage, à la frontière, par le constructeur compact du `record` qui la reçoit.
- **[PERMISSIONS]** Chaque route déclare son authentification ; une route publique le dit et le
  justifie, et elle est limitée en débit.
- **[SQL]** Jamais de requête SQL construite par concaténation : requêtes paramétrées.
- **[TRANSACTIONS]** Toute écriture touchant plus d'une ligne ou d'une table est dans une
  transaction, au périmètre le plus court.
- **[VERROUS]** Places et paiements : verrouillage en base (`SELECT … FOR UPDATE`) ou mise à jour
  conditionnelle, jamais lecture-calcul-écriture en Java : deux passagers ne réservent pas la
  dernière place.
- **[TEMPS-CONSTANT]** Toute comparaison de jeton ou de secret se fait en temps constant
  (`MessageDigest.isEqual`), jamais par `equals`.
- **[ACCESSIBILITE]** Chaque champ a un `<label>`, chaque bouton-icône un `aria-label`, chaque
  image informative un `alt` ; tout est utilisable au clavier.

### Tests
- **[TESTS]** Toute classe de service a ses tests.
- **[NON-REGRESSION]** Toute correction de bug arrive avec le test qui l'aurait détectée.
- **[RESEAU]** Aucun test n'émet de requête réseau réelle.
- **[HORLOGE]** Tout test lié à une date utilise une `Clock` fixe.
- **[TESTS-PERMISSIONS]** Toute route protégée a un test qui vérifie le refus (401, 403).

### Dépendances
- **[NOUVELLE-DEPENDANCE]** Toute dépendance ou tout outil se décide en équipe : maintenu, de
  licence compatible, sans équivalent dans le JDK, pas de nom proche d'un paquet connu.
- **[DEPENDANCES-FIGEES]** Versions exactes, jamais de plage (`+`, `latest`, `[1.0,)`), et
  vérifiées par somme de contrôle quand l'outil le permet.

### IA
- **[IA-RESPONSABLE]** Le code produit par un agent obéit aux mêmes règles et à la même relecture
  que le code humain ; celui qui fusionne en répond.
- **[IA-CONFIDENTIALITE]** Aucun secret ni donnée personnelle réelle dans une conversation avec
  un agent : on travaille sur des données de test.

## Git & Commits

- **[PAS-SUR-MAIN]** Jamais de commit direct sur `main` : toujours une branche puis une pull
  request, fusionnée une fois la CI verte.
- **[BRANCHES]** Préfixes `feature/`, `fix/`, `chore/`, `docs/`, `style/`, `refactor/`,
  `hotfix/` ; minuscules, sans espaces (`feature/reservation-place`).
- **[COMMITS]** Petits et atomiques :
  - 10 fichiers au plus par commit ;
  - titre de 50 caractères au plus, en français, préfixé : `feat:`, `fix:`, `refactor:`,
    `style:`, `docs:`, `perf:`, `test:`, `build:`, `ci:`, `chore:`, `revert:`.
- N'ajoute jamais de mention `Co-Authored-By: Claude` ni `Claude-Session:` dans les messages de commit.
- N'utilise jamais `--author` : l'auteur des commits reste celui configuré dans `git config`.
