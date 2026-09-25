# annway

Covoiturage pour les sorties annski : proposer un trajet vers la station, réserver une place.

Ce dépôt est **indépendant** d'annski : il ne dépend d'aucun de ses fichiers et ne doit pas
supposer qu'annski est présent sur la machine.

## Stack

Pas encore choisie. Quand elle le sera, la décrire ici (langages, frameworks, commandes pour
lancer, tester et vérifier le code).

## Langue

- Échanges, documentation et messages de commit en français ; code en anglais.

## Règles de code

Reprises de la charte d'annski (`GEMINI.md`), limitées à celles qui ne dépendent pas de la
stack. Une règle se cite par son identifiant entre crochets, jamais par un numéro de ligne.
Une règle impossible à tenir s'amende ici, avec sa raison ; on ne la contourne pas en silence.

En cas de conflit : **sécurité et intégrité des données > contrat d'API publié > lisibilité >
règles de forme**.

### Taille et découpage
- **[TAILLE-FICHIER]** 300 lignes au plus par fichier (hors fichiers générés et migrations).
  Un fichier qui dépasse devient un dossier ou un paquet.
- **[TAILLE-FONCTION]** Une fonction = une responsabilité, 30 lignes au plus (hors template,
  hors docstring). Un test peut aller jusqu'à 40 lignes.
- **[UNE-CLASSE]** Une classe par fichier, sauf les schémas, les exceptions métier et les
  classes de test d'un même domaine.

### Nommage
- **[NOMS-INTERDITS]** Jamais de noms génériques : `data`, `res`, `val`, `tmp`, `info`, `obj`,
  `item` seul, `result` seul, `x`. Le nom dit ce que contient la variable
  (`seat_count`, `trip_price_cents`).
- **[VOCABULAIRE]** Le code est en anglais (`trips`, `rides`, `seats`, `drivers`), les textes
  affichés en français.
- **[BOOLEENS]** Un booléen est préfixé : `is`, `has`, `should`, `can`.
- **[ZERO-MAGIE]** Pas de littéral métier en dur (statut, rôle, seuil, délai, prix) : constante
  nommée ou enum. `0`, `1`, `-1`, `""` et les littéraux des tests ne sont pas magiques.

### Argent, dates, données personnelles
- **[ARGENT]** Tout montant est un entier en centimes (`*_cents`), dans la base, l'API et le
  front. Jamais de flottant ; conversion en euros uniquement à l'affichage.
- **[FUSEAU]** Stockage en UTC ; toute notion calendaire (« aujourd'hui », date de départ) se
  calcule en `Europe/Paris`.
- **[DONNEES-PERSO]** Aucune donnée personnelle (e-mail, nom, téléphone, adresse, IP complète)
  dans les logs, les messages d'erreur, les URL ou les clés de cache : on journalise
  l'identifiant, pas la personne.
- **[SECRETS]** Aucun secret dans le code ni dans le dépôt : tout passe par l'environnement.
  Un secret commis par erreur est considéré compromis et changé.

### Code
- **[TYPAGE]** Code typé de bout en bout (TypeScript strict, annotations Python vérifiées) ;
  pas de `any`, `unknown` puis rétrécissement.
- **[ENTREES]** Toute donnée qui entre (requête, API tierce, stockage local, paramètre d'URL)
  est validée par un schéma avant usage.
- **[ETATS-UI]** Tout écran qui charge des données traite le chargement, l'erreur (message
  compréhensible) et la liste vide.
- **[ACCESSIBILITE]** Chaque champ a un `<label>`, chaque bouton-icône un `aria-label`, chaque
  image informative un `alt` ; tout est utilisable au clavier.
- **[PERMISSIONS]** Chaque route déclare son authentification ; une route publique le dit et
  le justifie, et elle est limitée en débit.
- **[TRANSACTIONS]** Toute écriture touchant plus d'une ligne ou d'une table est dans une
  transaction.
- **[VERROUS]** Places et paiements : verrouillage en base, jamais lecture-calcul-écriture en
  code applicatif (deux passagers ne réservent pas la dernière place).
- **[TEMPS-CONSTANT]** Toute comparaison de jeton ou de secret se fait en temps constant,
  jamais par `==`.

### Tests
- **[NON-REGRESSION]** Toute correction de bug arrive avec le test qui l'aurait détectée.
- **[RESEAU]** Aucun test n'émet de requête réseau réelle.
- **[HORLOGE]** Tout test lié à une date fige l'horloge.
- **[TESTS-PERMISSIONS]** Toute route protégée a un test qui vérifie le refus (401, 403).

### Dépendances
- **[DEPENDANCES-FIGEES]** Fichier de verrouillage commité ; installation en mode figé.
- **[NOUVELLE-DEPENDANCE]** Ajouter une dépendance se justifie dans le commit : maintenue,
  licence compatible, pas d'équivalent déjà présent, pas de nom proche d'un paquet connu.

### IA
- **[IA-RESPONSABLE]** Le code produit par un agent obéit aux mêmes règles et à la même
  relecture que le code humain ; celui qui fusionne en répond.
- **[IA-CONFIDENTIALITE]** Aucun secret ni donnée personnelle réelle dans une conversation
  avec un agent : on travaille sur des données de test.

## Git & Commits

- **[PAS-SUR-MAIN]** Jamais de commit direct sur `main` : toujours une branche puis une pull
  request.
- **[BRANCHES]** Préfixes `feature/`, `fix/`, `chore/`, `docs/`, `style/`, `refactor/`,
  `hotfix/` ; minuscules, sans espaces (`feature/reservation-place`).
- **[COMMITS]** Petits et atomiques :
  - 10 fichiers au plus par commit ;
  - titre de 50 caractères au plus, en français, préfixé : `feat:`, `fix:`, `refactor:`,
    `style:`, `docs:`, `perf:`, `test:`, `build:`, `ci:`, `chore:`, `revert:`.
- N'ajoute jamais de mention `Co-Authored-By: Claude` ni `Claude-Session:` dans les messages de commit.
- N'utilise jamais `--author` : l'auteur des commits reste celui configuré dans `git config`.
- Ne jamais commiter de `.env` : seul un `.env.example` sans valeur réelle est suivi.
