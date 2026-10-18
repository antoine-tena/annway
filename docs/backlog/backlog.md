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
  **Next free: T-04.**
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
- **Status**: blocked — waiting for `Trip` (Antoine, part 2)
- **Opened**: 2026-10-15
- **Effort**: S · **Owner**: Nathan · **Due**: —
- **Origin**: `REPARTITION.md` part 3 · `DiagrammeClasses.md` · was AF-03

- [ ] `TripFilter` strategy interface (`filter(List<Trip>)`), price filter, time-window filter,
  no-op filter
- [ ] Price filter in integer cents (`maxPriceCents`), not the `double` of the diagram
  ([ARGENT]); price of a Polytech trip (free donation) to agree on with Antoine
- [ ] Time bounds computed in `Europe/Paris` ([FUSEAU])
- [ ] Example tests, plus property tests (`jqwik`): a filter never returns more trips than it
  receives ([TESTS-PROPRIETES], dependency to decide as a team)

### T-03 · Trip catalogue and alerts (Observer pattern)
- **Type**: task
- **Status**: blocked — waiting for `Trip` and `SearchCriteria` (Antoine, part 2)
- **Opened**: 2026-10-15
- **Effort**: M · **Owner**: Nathan · **Due**: —
- **Origin**: `REPARTITION.md` part 3 · `DiagrammeClasses.md` · was AF-04

After T-02: the catalogue uses a `TripFilter`. `Passenger` (Pavlo) is already in `develop`.
- [ ] Subject and observer interfaces (subscribe, unsubscribe, notify / update)
- [ ] `TripCatalogue`: adding a trip notifies observers, search, filter strategy setter
- [ ] `TripAlert`: tells its `Passenger` when a trip matches its criteria, only while active
- [ ] Tests: subscribe, unsubscribe, inactive alert not notified

## 🟡 Normal

## ⚪ Background — long-running work, debt and ideas

---

## ✅ Done

### Log

One line per closed ticket, most recent first:
`- **YYYY-MM-DD** · T-NN · Title — what was done (branch) · opened YYYY-MM-DD`.

- **2026-10-15** · AF-02 · Equipment factory (Simple Factory) — `Equipment`, `Ski`, `Snowboard`, `Sled`, `EquipmentType`, `EquipmentFactory` and their tests, in `fr.annway.equipment` (feat/fabrique-materiel) · opened 2026-10-15
