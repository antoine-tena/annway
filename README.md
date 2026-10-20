# annway

Covoiturage pour les sorties annski : proposer un trajet vers la station, réserver une place.

## Démarrage rapide

Après le clone, depuis la racine du dépôt (prérequis : [uv](https://docs.astral.sh/uv/), un JDK 25, `unzip` et [pnpm](https://pnpm.io/) avec Node 24) :

```sh
uv tool install kiln-cli   # une fois par poste
kiln install
kiln dev
```

Puis ouvrir http://localhost:3000.

## Équipe

- [@antoine-tena](https://github.com/antoine-tena)
- [@NathanBarrachin](https://github.com/NathanBarrachin)
- [@Trofymov-Pavlo](https://github.com/Trofymov-Pavlo)

## Organisation

- `backend/` : le backend Java 25 (Maven, sans framework) ; l'API web dans `fr.annway.web`.
- `frontend/` : l'interface, en Nuxt 4 + Vue 3.
- [`CLAUDE.md`](CLAUDE.md) et [`.claude/rules/kiln/`](.claude/rules/kiln/) : la charte du code
  et la façon de travailler, pour les humains comme pour les agents.
- `.vscode/` : réglages et extensions recommandées, partagés par toute l'équipe.

Le projet est généré et tenu à jour par [kiln](https://github.com/antoine-tena/kiln). Changer un
paramètre (équipe, frameworks, podman…) : `kiln set <clé>=<valeur>`, sur une branche.

## Installation

Prérequis : ceux du démarrage rapide.
`kiln install` installe les dépendances (backend, frontend) et active les garde-fous du
commit (pre-commit, dont le refus de commiter sur `main` ou `develop`). On peut la relancer sans
risque.

## Lancer le projet

```sh
kiln dev
```

Lance l'API Java (son jar, reconstruit à chaque lancement, sur http://localhost:8080) et le front
sur http://localhost:3000, qui sert l'API sous `/api/` par mandataire : le cookie de session suit
sans CORS. Les réglages locaux de l'API sont dans `backend/.env` (ignoré par git), à créer depuis
`backend/.env.example` :

- `ANNWAY_PORT` : port de l'API (8080 par défaut).
- `ANNWAY_DEMO_PASSWORD` : facultatif, propre à chaque poste ; donne ce mot de passe aux comptes de démonstration,
  fictifs (`camille@example.org` et `hugo@example.org`, conducteurs ; `lea@example.org` et
  `malo@example.org`, passagers). Sans lui, on crée son compte depuis « Inscription ».
- `ANNWAY_API_ORIGIN` (front) : origine de l'API, `http://localhost:8080` par défaut.
- Les données vivent en mémoire : elles repartent de la démonstration à chaque redémarrage.

## L'API

Paquet `fr.annway.web` : serveur HTTP du JDK, sans dépendance. Les écritures arrivent en
formulaire (`application/x-www-form-urlencoded`), les réponses partent en JSON, les montants
en centimes et les instants en UTC. Chaque route déclare son accès (`Access`) : publique et
limitée en débit, membre connecté, passager ou conducteur ; une erreur répond
`{"code", "message", "field"}`.

Une réservation payée (prix fixe ou don) est remboursée en entier quand le conducteur la refuse
ou annule le trajet, et quand le passager l'annule au moins 24 h avant le départ ; plus tard,
elle ne l'est pas. Le prestataire de paiement est celui de démonstration (`DemoPaymentApi`).

| Domaine | Routes |
| --- | --- |
| Comptes | `POST /api/auth/register`, `/login`, `/logout` ; `GET /api/me` ; `POST /api/me/profile`, `/api/me/verifications/{email|phone}` (code à usage unique, renvoyé dans la réponse faute d'envoi branché), `…/confirm`, `/api/me/vehicles`, `/api/me/reviews/{index}/reply` ; `GET /api/users/{id}`, `POST /api/users/{id}/reviews` |
| Trajets | `GET /api/trips` (recherche et filtres), `/api/trips/{id}`, `/api/me/trips` ; `POST /api/trips`, `/api/trips/{id}/stops`, `/cancel`, `/finish` |
| Réservations | `POST /api/trips/{id}/reservations` ; `GET /api/me/reservations` ; `POST /api/reservations/{id}/pay`, `/cancel`, `/validate`, `/refuse` |
| Alertes et demandes | `GET`/`POST /api/me/alerts`, `POST /api/me/alerts/{index}/toggle` ; `GET`/`POST /api/me/trip-requests` ; `GET /api/trip-requests` |
| Messages | `GET`/`POST /api/trips/{id}/messages` |
| Divers | `GET /api/health`, `/api/equipment-types` |

## Le front

`frontend/` reprend le thème d'annski (mêmes jetons sémantiques dans `tokens.css`, mêmes
polices, même en-tête noir) en noir et bleu, pour qu'annway la rejoigne un jour comme deuxième
plateforme. Pages : accueil, catalogue et recherche (`/trajets`), détail d'un trajet
(réservation, messages, gestion par le conducteur), publication, connexion et inscription,
compte, réservations, alertes et demandes, demandes reçues par les conducteurs, profil d'un
membre.

Le serveur Nitro (`frontend/server/plugins/securityHeaders.ts`) envoie les en-têtes de sécurité
sur chaque réponse hors `/api/`, et, sur le front construit (`pnpm build`), une
Content-Security-Policy stricte : aucune origine tierce, un nonce par page sur les scripts que
Nuxt écrit en ligne. `kiln dev` s'en passe, Vite injectant son code en ligne.

## Vérifier le code

```sh
kiln check
```

`./mvnw verify` pour le backend Java (compilation stricte, JUnit, couverture), ESLint et le contrôle des types côté frontend, puis la charte (`charter`).

Côté front, dans `frontend/` :

- `pnpm test` : tests unitaires Vitest (`app/utils/*.spec.ts`, `server/**/*.spec.ts`), lancés
  aussi par la CI ;
- `pnpm e2e` : tests de bout en bout Playwright (`e2e/`) contre la pile complète, que la commande
  démarre elle-même (API Java sur le port 8180, front construit sur le 3100) : réserver, payer et
  valider, l'audit axe-core (WCAG 2.2 AA) des pages principales, les en-têtes de sécurité et la
  CSP. Une seule fois par poste avant le premier lancement : `pnpm exec playwright install chromium`.

## Mettre à jour

Sur une branche dédiée, avec un arbre propre : `kiln update` (mises à jour compatibles) ou
`kiln update --major` (versions majeures comprises). La commande rapporte les évolutions de kiln
sans écraser les modifications locales, met à jour les dépendances, puis lance les contrôles.
Avec Claude Code, le skill `/kiln:update` fait tout : l'outil kiln, puis le dépôt.

## Suivi du travail

Ce qui reste à faire est dans [`docs/backlog/`](docs/backlog/) : `backlog.md` (les tickets, par
priorité), `rejected.md` (ce qui a été refusé) et `urgent.md` (ce que seul un humain peut faire).

## Contribuer

1. Créer une branche depuis `develop` : `feat/…`, `fix/…` ([BRANCHES]).
2. Respecter la charte ([`.claude/rules/kiln/`](.claude/rules/kiln/), [`CLAUDE.md`](CLAUDE.md)).
3. Fusionner la branche dans `develop` (`git merge --no-ff`, puis push), sans attendre
   d'approbation : skill `/kiln:merge` avec Claude Code. Une pull request reste possible pour
   demander un avis. On ne commite jamais directement sur `main` ou `develop`.

## Déploiement

Actions › Déploiement › Run workflow, depuis `main` :
https://github.com/antoine-tena/annway/actions/workflows/deploy.yml. La CI est rejouée avant
toute mise en production.
