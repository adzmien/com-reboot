Status: in-progress 2026-05-07

## Spec Reference

ISSUE-3 from `docs/specs/spec-reboot-uam.md`

## What to build

Implement the full authentication flow in `reboot-auth-service`. `AuthTokenService` issues and validates JWTs (userId, role, email claims; 15 min TTL) and manages refresh tokens in Redis (7-day TTL). `AccountLockoutManager` increments a Redis counter on each failed login; on threshold, sets a TTL-based lock key and writes `failed_attempt_count` and `locked_until` to DB. On successful login, resets both. DB is the fallback if Redis is cold. `LoginService` ties these together: verifies bcrypt password, creates session, handles logout and refresh.

## Acceptance Criteria

- [ ] Valid credentials return `200` with `access_token` (JWT) and `refresh_token` in body
- [ ] Invalid password returns `401` — response is identical to unknown email (no user enumeration)
- [ ] After N failed attempts (N configurable), the account is locked — returns `423` with `AUTH-001` error code
- [ ] A locked account returns `423` even with the correct password until the lock TTL expires
- [ ] Successful login resets the failed attempt counter — a subsequent failure starts the count from 1
- [ ] `POST /auth/logout` with a valid refresh token returns `200`; the same token then returns `401` on `POST /auth/refresh`
- [ ] `POST /auth/refresh` with a valid refresh token returns `200` with a new `access_token`
- [ ] `POST /auth/refresh` with an expired or unknown refresh token returns `401`
- [ ] On first login with `force_password_change = true`, response includes `force_password_change: true` flag

## Blocked by

- #1
