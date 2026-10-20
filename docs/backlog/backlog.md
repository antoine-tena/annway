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
  **Next free: T-08.**
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

### T-01 · Ticket title
- **Type**: task
- **Status**: todo
- **Opened**: 2026-10-15
- **Effort**: S · **Owner**: — · **Due**: —
- **Origin**: —

What to do, why, where to look.

## 🟠 Important

### T-03 · Trip catalogue and alerts (Observer pattern)
- **Type**: task
- **Status**: todo
- **Opened**: 2026-10-15
- **Effort**: M · **Owner**: Nathan · **Due**: —
- **Origin**: `REPARTITION.md` part 3 · `DiagrammeClasses.md` · was AF-04

After T-02: the catalogue uses a `TripFilter`. `Passenger` (Pavlo) is already in `develop`.
- [ ] Subject and observer interfaces (subscribe, unsubscribe, notify / update)
- [ ] `TripCatalogue`: adding a trip notifies observers, search, filter strategy setter
- [ ] `TripAlert`: tells its `Passenger` when a trip matches its criteria, only while active
- [ ] Tests: subscribe, unsubscribe, inactive alert not notified

Use cases « Rechercher un trajet » (includes « Saisir critère du voyage »: departure, arrival,
date, passengers) and « S'abonner alerte trajet ». Scenarios to test:
- [ ] Search: returns the trips that match the criteria (`Trip` matching, Antoine), then applies
  the current filter; changing the filter changes the next results
- [ ] Adding a trip notifies every subscribed observer once, with that trip
- [ ] An observer unsubscribed is no longer notified; subscribing twice notifies once
- [ ] Active alert + matching trip: the passenger is warned; non-matching trip or inactive
  alert: not warned; an alert can be switched off

Open questions, not answered by either diagram:
- [ ] How the passenger is warned: no class carries the notification. Proposal: the alert
  keeps the matching trips (`matchedTrips()`), the display comes later
- [ ] Who adds a published trip to the catalogue: « Publier un trajet » is `Driver` (Pavlo),
  the catalogue is mine; a service between them, rather than `Driver` knowing the catalogue
  ([LAYERS]), to agree with Pavlo
- [x] Full, finished or cancelled trips: out of search and alerts — `Trip.matches` only
  accepts an `OPEN` trip

### T-05 · Passenger and driver actions on trips (part 1)
- **Type**: task
- **Status**: todo
- **Opened**: 2026-10-19
- **Effort**: S · **Owner**: Pavlo · **Due**: —
- **Origin**: `DiagrammeClasses.md` · `REPARTITION.md` part 1

The methods of the diagram on `Passenger` and `Driver` (Pavlo's classes) can now be written; each
one only delegates to part 2 (`fr.annway.trip`):
- [ ] `Passenger.book(trip, seatCount, equipment)` → `Reservation.request(…)`
- [ ] `Passenger.createTripRequest(criteria)` → `TripRequest.create(…)`
- [ ] `Driver.validateReservation(r)` / `refuseReservation(r)` → `r.validate(this)` / `r.refuse(this)`
  (the reservation checks that this driver owns the trip)
- [ ] `Driver.publishTrip(trip)`: who adds it to the catalogue is still open in T-03
- [ ] `Passenger.searchTrip` / `subscribeAlert`: after T-03 (Nathan's catalogue)
- [ ] `Message.tripId` may become a `Trip` now that the class exists (Pavlo's call)

### T-06 · Refund when a paid booking is cancelled or refused
- **Type**: idea
- **Status**: todo
- **Opened**: 2026-10-19
- **Effort**: S · **Owner**: Antoine · **Due**: —
- **Origin**: T-04

`Reservation.cancel` gives the seats back but not the money; `PaymentService` has no refund.
To decide as a team: refund a fixed price always, never a donation? Up to which delay before
departure?

## 🟡 Normal

### T-07 · Property tests of the search filters
- **Type**: task
- **Status**: blocked — waiting for the team's decision on `jqwik` ([NEW-DEPENDENCY])
- **Opened**: 2026-10-20
- **Effort**: XS · **Owner**: Nathan · **Due**: —
- **Origin**: T-02

[PROPERTY-TESTS]: a filter never returns more trips than it receives, never adds one and keeps
the order. Today `TripFilterContractTest` checks it on examples; once `jqwik` is accepted, the
same invariants on generated lists.

## ⚪ Background — long-running work, debt and ideas

---

## ✅ Done

### Log

One line per closed ticket, most recent first:
`- **YYYY-MM-DD** · T-NN · Title — what was done (branch) · opened YYYY-MM-DD`.

- **2026-10-20** · T-02 · Search filters (Strategy pattern) — `TripFilter`, `PriceFilter` in cents, `TimeWindowFilter` in Paris time, `NoFilter`, `SearchException`, example tests; property tests in T-07 (feat/filtres-recherche) · opened 2026-10-15
- **2026-10-19** · T-04 · Bookings and payment (part 2) — `Reservation` (request, validate, refuse, cancel), `Payment` in cents, payment adapter, `TripRequest`, trip `FULL` and `FINISHED` (feat/reservations) · opened 2026-10-19
- **2026-10-15** · AF-02 · Equipment factory (Simple Factory) — `Equipment`, `Ski`, `Snowboard`, `Sled`, `EquipmentType`, `EquipmentFactory` and their tests, in `fr.annway.equipment` (feat/fabrique-materiel) · opened 2026-10-15
