# Backlog

Everything left to do on annway, in a single list ordered by priority. Started on
**2026-10-15**. Closed tickets are in [done.md](done.md), what was turned down in
[rejected.md](rejected.md); how to write a ticket: [README.md](README.md). **Nothing else is open.**

North star: **annway en service**.

**Next free: T-17.**

---

## 🔴 Critical

### T-16 · Finish the kiln update to v0.4.9
- **Type**: task
- **Status**: todo
- **Opened**: 2026-10-26
- **Effort**: XS · **Owner**: — · **Due**: —
- **Origin**: release notes of the kiln releases crossed (`kiln update`)

What kiln cannot guess, to fill in right after the update (`/kiln:update`):
- [ ] in a repo adopted before 0.4.6, run `kiln adopt` again on a work branch to lay the rest of the template; until then `kiln update` only moves its workflows' tag. (v0.4.6)


## 🟠 Important

## 🟡 Normal

## ⚪ Background — long-running work, debt and ideas

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
