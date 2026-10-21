# Backlog

Everything left to do on annway, in a single list ordered by priority. Started on
**2026-10-15**. Closed tickets are in [done.md](done.md), what was turned down in
[rejected.md](rejected.md); how to write a ticket: [README.md](README.md). **Nothing else is open.**

North star: **annway en service**.

**Next free: T-16.**

---

## 🔴 Critical


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
