# annway

Carpooling for annski outings: offer a trip to the station, book a seat.

## Repo map

- `backend/src/main/java/fr/annway/`: `Main.java`, one package per domain; `web/`: HTTP API (`WebMain`, routes per domain, sessions, demo data); `backend/src/test/java/`: JUnit; `backend/pom.xml`.
- `frontend/app/`: `pages/`, `components/` (`ui/`, `header/`, `trip/`, `account/`, `auth/`, `reservation/`), `composables/` (one per API domain), `assets/css/tokens.css` (annski theme in blue); `frontend/server/`: Nitro plugin (security headers, nonce CSP).
- `REPARTITION.md`: class ownership. `.github/workflows/`: `ci.yml` (kiln reusable workflows, gitleaks), `deploy.yml`.
- `docs/backlog/`; `charter.toml`; `.claude/rules/kiln/`.

## Notes

- Independent of annski: depends on none of its files, doesn't assume it's on the machine.
- Backend Java 25 LTS, no framework; front Nuxt/TS, reuses annski's tokens, header, login in black and blue (will join annski as second platform). Any other library or tool is a team decision ([NEW-DEPENDENCY]).
- Deploy: `deploy.yml`, manual from `main`, replays CI first.
- **[PERMISSIONS]** Each route declares auth; public route says so, justifies it, is rate-limited.
- **[PROPRIETAIRE-CLASSE]** Each class has one team owner (`REPARTITION.md`): nobody, human or agent, creates, edits or deletes another module's class (review).
- **[PERMISSION-TESTS]** Protected route has a refusal test (401, 403).
- **[ACCESSIBILITY]** Field `<label>`, icon button `aria-label`, informative image `alt`; keyboard usable.
