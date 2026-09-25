# annway

Covoiturage pour les sorties annski : proposer un trajet vers la station, réserver une place.

## Prérequis

- JDK 25 (Temurin recommandé). Gradle n'a pas à être installé : le projet utilise son wrapper.

## Lancer

```sh
./gradlew run     # lancer l'application
./gradlew test    # tests
./gradlew build   # tout vérifier, comme la CI
```

## Contribuer

1. Créer une branche depuis `main` (`feature/…`, `fix/…`).
2. Respecter les règles du [`CLAUDE.md`](CLAUDE.md) : elles valent pour tout le monde, humains
   et agents.
3. Ouvrir une pull request ; la CI doit être verte avant la fusion.

## Déploiement

Actions › Déploiement › Run workflow, depuis `main` : la CI est rejouée avant toute mise en
production.
