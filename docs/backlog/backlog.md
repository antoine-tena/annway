# Backlog

Everything left to do on annway, in a single list ordered by priority. Started on
**2026-10-15**. What is done is in the "✅ Done" section at the bottom; what was turned
down is in [rejected.md](rejected.md). **Nothing else is open.**

North star: **annway en service**.

---

## How to use (humans and agents)

**A ticket** = a `### T-NN · Title` heading, then five fixed lines, then the details:

    ### T-NN · Short title that says what to achieve
    - **Type**: task
    - **Status**: todo
    - **Opened**: YYYY-MM-DD
    - **Effort**: S · **Owner**: … · **Due**: —
    - **Origin**: where it comes from · audit `docs/_archive/<folder>/README.md` l. 10-20

    Why, what to do (as a `- [ ]` checklist when there are several steps), where to look.

- **ID** `T-NN`: stable, never reused, quoted as is in commits and branches.
  **Next free: T-15.**
- **Type**, one of: `bug` (something is broken); `improvement` (it works, it could work
  better); `idea` (nothing exists yet, to be explored); `task` (work that is neither: a release,
  a decision, a check).
- **Status**, one of: `todo`; `in progress — branch <name>, since YYYY-MM-DD`;
  `blocked — waiting for <who or what>`; `to verify — <where>`.
- **Priority** = the section the ticket lives in; changing it means moving the ticket.
  🔴 critical (now), 🟠 important (before the north star), 🟡 normal, ⚪ background
  (long-running work, debt, ideas still to be explored).
- **Effort**: XS (under an hour), S (half a day), M (one or two days), L (more).
- **Owner**: `—` (free, anyone may take it), a member's GitHub login (`@login`) or first name
  (the ticket is reserved for them), `Claude` (an agent's job), or `contributor — #NN` (kept for
  an outside contributor). An agent only works on a free ticket or one owned by the account it
  works for (`gh api user --jq .login`): it skips the others and, asked for one, says who owns
  it instead of doing it. **Each member writes only their own login**: taking a free ticket, or
  turning their own first name into their login; never someone else's (only the owner hands a
  ticket over).
- **Releases**: the `## 🚀 Releases` section at the top places every open ticket in a planned
  release (`/kiln:backlog plan`); numbers are planned, not promised, until the candidate is cut.
- **Closing a ticket**: remove it from its section and add a line at the top of the "✅ Done"
  log: `- **YYYY-MM-DD** · T-NN · Title — what was done (branch) · opened YYYY-MM-DD`.
- **Details in archived audits**: when a ticket quotes lines of `docs/_archive/`, the original
  finding is there in full; it is old, check it against the current code.
- **Locations quoted are hints, not addresses.**
- Overview: `grep -E '^(## |### T-|- \*\*Status)' docs/backlog/backlog.md`.

---

## 🔴 Critical

## 🟠 Important

### T-09 · Persist the data of the API
- **Type**: task
- **Status**: todo
- **Opened**: 2026-10-20
- **Effort**: M · **Owner**: Antoine · **Due**: —
- **Origin**: branch feat/front-nuxt

`fr.annway.web.AnnwayStore` keeps accounts, trips, reservations and sessions in memory: a
restart brings back the demo data and nothing else. A database (PostgreSQL through podman, JDBC
of the JDK) is a team decision ([NEW-DEPENDENCY]); then [SQL], [TRANSACTIONS] and [LOCKS] on
seats and payments, and the export and anonymisation of [PERSONAL-DATA].

### T-10 · A real payment provider
- **Type**: task
- **Status**: todo
- **Opened**: 2026-10-20
- **Effort**: M · **Owner**: Antoine · **Due**: —
- **Origin**: branch feat/front-nuxt

`fr.annway.web.DemoPaymentApi` accepts every payment without charging anything. Choose a
provider as a team, implement `ExternalPaymentApi` with its key in `.<service>/` and the
environment ([SECRETS]); refunds go through `RefundService` (T-06).

## 🟡 Normal

### T-13 · Profile verifications
- **Type**: idea
- **Status**: todo
- **Opened**: 2026-10-20
- **Effort**: M · **Owner**: — · **Due**: —
- **Origin**: branch feat/front-nuxt

`Profile` knows the email, phone and ID verifications, the front shows them, but nothing sets
them (only the demo data). To decide as a team: link sent by email, code by SMS, ID checked by
whom?

## ⚪ Background — long-running work, debt and ideas

---

## ✅ Done

### Log

One line per closed ticket, most recent first:
`- **YYYY-MM-DD** · T-NN · Title — what was done (branch) · opened YYYY-MM-DD`.

- **2026-10-20** · T-12 · Front tests and end-to-end journeys — Vitest, Playwright and axe-core chosen: unit tests of `utils/` (cents, Paris time, criteria) and of the security headers in CI (`pnpm test`), Playwright journey "book, pay, validate", axe-core audit of the main pages and CSP check against the full stack (`pnpm e2e`); test density floor raised to 24 (test/t12-front-tests) · opened 2026-10-20
- **2026-10-20** · T-06 · Refund when a paid booking is cancelled or refused — rule decided: full refund (fixed price or donation) on refusal or trip cancellation, and on a passenger cancellation at least 24 h before departure, none later; `RefundService` next to `PaymentService`, `Refund`, status `TRIP_CANCELLED` closing the bookings of a cancelled trip, shown on the front (feat/t06-refund) · opened 2026-10-19
- **2026-10-20** · T-11 · Strict CSP by nonce for the front — Nitro plugin `server/plugins/securityHeaders.ts`: the [SECURITY-HEADERS] on every response outside `/api/`, a CSP with no third-party origin and a nonce per page on the built front, checked in the browser; its automated test joins T-12 (feat/t11-csp-nonce) · opened 2026-10-20
- **2026-10-20** · T-07 · Property tests of the search filters — `TripFilterPropertiesTest`: the filter contract (never more trips, none added, order kept) on 1000 generated lists; `jqwik` 1.10.2 accepted by the team, runs on JUnit 6 (test/jqwik-junit6) · opened 2026-10-20
- **2026-10-20** · T-14 · `kiln dev` starts the Java API — fixed in kiln (v0.4.1-rc.2): the jar runs next to the front with `backend/.env`; annway's jar starts `fr.annway.web.WebMain` (fix/dev-api) · opened 2026-10-20
- **2026-10-20** · T-05 · Passenger and driver actions on trips (part 1) — `Passenger.searchTrip`, `book`, `createTripRequest`, `subscribeAlert`; `Driver.publishTrip`, `validateReservation`, `refuseReservation`, each delegating to the trip and catalogue classes; `Message` now refers to its `Trip` (feat/actions-trajets) · opened 2026-10-19
- **2026-10-20** · T-08 · Who adds a published trip to the catalogue — `TripCatalogue.publish(driver, trip)`: only the trip's driver, once, alerts warned; `Driver.publishTrip` can delegate to it (T-05) (feat/publication-catalogue) · opened 2026-10-20
- **2026-10-20** · T-03 · Trip catalogue and alerts (Observer pattern) — `TripSubject`, `TripObserver`, `TripCatalogue` (search then filter, notifies on add), `TripAlert` (keeps `matchedTrips()` while active) and their tests; publishing link in T-08 (feat/catalogue-alertes) · opened 2026-10-15
- **2026-10-20** · T-02 · Search filters (Strategy pattern) — `TripFilter`, `PriceFilter` in cents, `TimeWindowFilter` in Paris time, `NoFilter`, `SearchException`, example tests; property tests in T-07 (feat/filtres-recherche) · opened 2026-10-15
- **2026-10-19** · T-04 · Bookings and payment (part 2) — `Reservation` (request, validate, refuse, cancel), `Payment` in cents, payment adapter, `TripRequest`, trip `FULL` and `FINISHED` (feat/reservations) · opened 2026-10-19
- **2026-10-15** · AF-02 · Equipment factory (Simple Factory) — `Equipment`, `Ski`, `Snowboard`, `Sled`, `EquipmentType`, `EquipmentFactory` and their tests, in `fr.annway.equipment` (feat/fabrique-materiel) · opened 2026-10-15
