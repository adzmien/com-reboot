# Login, JWT Issuance & Token Refresh

## Spec Reference

ISSUE-3 from `docs/specs/spec-reboot-uam.md`

## What to build

`reboot-auth-service` Flyway schema (`uam_auth`: `refresh_tokens`, `outbox`, `processed_events`). Internal credential endpoint in `reboot-uam-service` (Feign target for auth-service). Login endpoint: validate credentials via Feign call to `uam-service`, verify bcrypt hash, issue a JWT (`jti`, `sub`, `roles`, `permissions`, `exp=15min`), persist refresh token + outbox entry in a single DB transaction. Token refresh endpoint. Fully implement `OutboxRelay` in `reboot-auth-service`.

## Acceptance Criteria

- [ ] Valid email + password returns `200` with `accessToken` (JWT) and `refreshToken`
- [ ] The returned JWT contains `sub`, `roles`, `permissions`, and `exp` claims
- [ ] Wrong password returns `401` with a response body identical to unknown email (no user enumeration)
- [ ] Unknown email returns `401`
- [ ] A valid `refreshToken` submitted to `POST /auth/refresh` returns a new `accessToken`
- [ ] An expired or invalid `refreshToken` returns `401`
- [ ] **Contract:** `UserLoggedIn` event on `auth.events` is deserializable with fields: `userId`, `timestamp`

## Blocked by

- #2
