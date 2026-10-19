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
  **Next free: T-07.**
- **Type**, one of: `bug` (something is broken); `improvement` (it works, it could work
  better); `idea` (nothing exists yet, to be explored); `task` (work that is neither: a release,
  a decision, a check).
- **Status**, one of: `todo`; `in progress — branch <name>, since YYYY-MM-DD`;
  `blocked — waiting for <who or what>`; `to verify — <where>`.
- **Priority** = the section the ticket lives in; changing it means moving the ticket.
  🔴 critical (now), 🟠 important (before the north star), 🟡 normal, ⚪ background
  (long-running work, debt, ideas still to be explored).
- **Effort**: XS (under an hour), S (half a day), M (one or two days), L (more).
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

### T-02 · Search filters (Strategy pattern)
- **Type**: task
- **Status**: todo
- **Opened**: 2026-10-15
- **Effort**: S · **Owner**: Nathan · **Due**: —
- **Origin**: `REPARTITION.md` part 3 · `DiagrammeClasses.md` · was AF-03

- [ ] `TripFilter` strategy interface (`filter(List<Trip>)`), price filter, time-window filter,
  no-op filter
- [ ] Price filter in integer cents (`maxPriceCents`), not the `double` of the diagram
  ([ARGENT]): `Trip.amountDueCents(1)`, which is 0 for a Polytech trip (free donation)
- [ ] Time bounds computed in `Europe/Paris` ([FUSEAU])
- [ ] Example tests, plus property tests (`jqwik`): a filter never returns more trips than it
  receives ([TESTS-PROPRIETES], dependency to decide as a team)

Use case « Filtrer les résultats (prix, horaire) », an optional extension of « Rechercher un
trajet »: without a filter the search returns everything. Scenarios to test:
- [ ] No filter: every trip received comes back, in the same order
- [ ] Price: keeps the trips whose amount due for one seat is at most `maxPriceCents`, bound
  included; a Polytech trip (donation, nothing due) is always kept
- [ ] Time window: keeps the trips leaving between `earliest` and `latest`, Paris local time,
  bounds included; a window with `earliest` after `latest` is refused (business error)
- [ ] Every filter: empty list in, empty list out; never adds a trip; keeps the order

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
  ([COUCHES]), to agree with Pavlo
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

## ⚪ Background — long-running work, debt and ideas

---

## ✅ Done

### Log

One line per closed ticket, most recent first:
`- **YYYY-MM-DD** · T-NN · Title — what was done (branch) · opened YYYY-MM-DD`.

- **2026-10-19** · T-04 · Bookings and payment (part 2) — `Reservation` (request, validate, refuse, cancel), `Payment` in cents, payment adapter, `TripRequest`, trip `FULL` and `FINISHED` (feat/reservations) · opened 2026-10-19
- **2026-10-15** · AF-02 · Equipment factory (Simple Factory) — `Equipment`, `Ski`, `Snowboard`, `Sled`, `EquipmentType`, `EquipmentFactory` and their tests, in `fr.annway.equipment` (feat/fabrique-materiel) · opened 2026-10-15
