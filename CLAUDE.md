# annway

Covoiturage pour les sorties annski : proposer un trajet vers la station, réserver une place.

<!-- This file belongs to the project: kiln wrote it once and never changes it. kiln's rules live
in .claude/rules/kiln/ and are kept current by `kiln update`. Write here what Claude cannot
guess about this project, and keep it under 200 lines: every session reads it. -->

## Repo map

Where to look, so as not to explore: read it before opening files, and update it in the commit
that adds, moves or removes a directory, a domain or an entry point. One line per place, what
it holds; no copy of the code.

- `backend/src/main/java/fr/annway/`: entry point (`Main.java`), one
  package per domain; `backend/src/test/java/`: JUnit tests; `backend/pom.xml`: Maven build.
- `docs/backlog/`: the work to do; `charter.toml`: the checked rules; `.claude/rules/kiln/`:
  kiln's rules for Claude.

## Notes

<!-- Decisions, conventions and pitfalls of this project that Claude cannot guess from the code:
"we never call X directly, always through Y", "the staging database is reset every night"… -->

## Carried over from the instructions of kiln 0.3

### From `CLAUDE.md`

## Propre à annway

- Ce dépôt est **indépendant** d'annski : il ne dépend d'aucun de ses fichiers et ne suppose pas
  qu'annski est présent sur la machine.
- **Tout le projet est en Java** (Java 25 LTS), aucun autre langage applicatif. Aucun
  framework : toute bibliothèque ou tout outil se décide en équipe ([NEW-DEPENDENCY]).
- Déploiement : `.github/workflows/deploy.yml`, lancé à la main depuis `main`, qui rejoue la CI
  avant toute mise en production.
- **[PERMISSIONS]** Chaque route déclare son authentification ; une route publique le dit et le
  justifie, et elle est limitée en débit.
- **[PROPRIETAIRE-CLASSE]** Chaque classe appartient à un seul membre de l'équipe, selon
  [`REPARTITION.md`](REPARTITION.md) : personne, humain ou agent, ne crée, ne modifie ni ne
  supprime une classe d'un autre module (revue).
- **[PERMISSION-TESTS]** Toute route protégée a un test qui vérifie le refus (401, 403).
- **[ACCESSIBILITY]** Chaque champ a un `<label>`, chaque bouton-icône un `aria-label`, chaque
  image informative un `alt` ; tout est utilisable au clavier.

- `REPARTITION.md` : quel membre de l'équipe possède quelles classes.

- `.github/workflows/` : CI (`ci.yml`, workflows réutilisables de kiln et scan gitleaks) et
  déploiement manuel (`deploy.yml`).
