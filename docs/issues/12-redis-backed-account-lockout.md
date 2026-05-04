# Redis-Backed Account Lockout

## Spec Reference

ISSUE-12 from `docs/specs/spec-reboot-uam.md`

## What to build

`reboot-auth-service` login path: on each failed attempt, `INCR failed_attempts:{userId}` in Redis via `AccountLockoutGuard`. When count >= `auth.lockout.threshold`: write locked status to DB + publish `AccountLocked` via `OutboxRelay` (single transaction), and `SET account_locked:{userId} EX {window}` in Redis. Fast-path lockout check at login start (Redis first, DB fallback if Redis is unavailable). Return `423` on locked accounts. `reboot-uam-service` `IdempotentConsumer` for `account.locked` → update `internal_user.status = LOCKED`.

## Acceptance Criteria

- [ ] After N-1 failed login attempts, login still returns `401` (not yet locked)
- [ ] After exactly N failed attempts, login returns `423`
- [ ] While locked, any further login attempt returns `423` regardless of correct password
- [ ] After the lockout window elapses, a correct password returns `200`
- [ ] After the `account.locked` event is consumed, `GET /users/{id}` (admin) reflects `status=LOCKED`
- [ ] **Contract:** `AccountLocked` event on `account.locked` is deserializable by `uam-service` with fields: `userId`, `lockedAt`

## Blocked by

- #3
- #6
