# annway

Covoiturage pour les sorties annski : proposer un trajet vers la station, réserver une place.

Ce dépôt est **indépendant** d'annski : il ne dépend d'aucun de ses fichiers et ne doit pas
supposer qu'annski est présent sur la machine.

## Stack

Pas encore choisie. Quand elle le sera, la décrire ici (langages, frameworks, commandes pour
lancer, tester et vérifier le code).

## Langue

- Échanges, documentation et messages de commit en français.

## Git & Commits

- Ne jamais commiter directement sur `main` : toujours passer par une branche
  (`feat/…`, `fix/…`, `docs/…`, `chore/…`) puis une pull request.
- Messages de commit au format Conventional Commits, en français
  (ex. `feat(trajets): réservation d'une place`).
- N'ajoute jamais de mention `Co-Authored-By: Claude` ni `Claude-Session:` dans les messages de commit.
- N'utilise jamais `--author` : l'auteur des commits reste celui configuré dans `git config`.

## Secrets

- Ne jamais commiter de `.env` ni de clé : seul un `.env.example` sans valeur réelle est suivi.
