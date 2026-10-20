# annway

Covoiturage pour les sorties annski : proposer un trajet vers la station, réserver une place.

## Démarrage rapide

Après le clone, depuis la racine du dépôt (prérequis : [uv](https://docs.astral.sh/uv/), un JDK 25 et `unzip`) :

```sh
uv tool install kiln-cli   # une fois par poste
kiln install
```

Puis vérifier le backend : `cd backend && ./mvnw verify`.

## Équipe

- [@antoine-tena](https://github.com/antoine-tena)
- [@NathanBarrachin](https://github.com/NathanBarrachin)
- [@Trofymov-Pavlo](https://github.com/Trofymov-Pavlo)

## Organisation

- `backend/` : le backend Java 25 (Maven, sans framework).
- [`CLAUDE.md`](CLAUDE.md) et [`.claude/rules/kiln/`](.claude/rules/kiln/) : la charte du code
  et la façon de travailler, pour les humains comme pour les agents.
- `.vscode/` : réglages et extensions recommandées, partagés par toute l'équipe.

Le projet est généré et tenu à jour par [kiln](https://github.com/antoine-tena/kiln). Changer un
paramètre (équipe, frameworks, podman…) : `kiln set <clé>=<valeur>`, sur une branche.

## Installation

Prérequis : ceux du démarrage rapide.
`kiln install` installe les dépendances (backend) et active les garde-fous du
commit (pre-commit, dont le refus de commiter sur `main` ou `develop`). On peut la relancer sans
risque.

## Lancer le projet

```sh
cd backend && ./mvnw -q package -DskipTests && java -jar target/annway-0.1.0.jar
```

## Vérifier le code

```sh
kiln check
```

`./mvnw verify` pour le backend Java (compilation stricte, JUnit, couverture), puis la charte (`charter`).

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
