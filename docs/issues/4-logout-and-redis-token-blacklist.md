# Logout & Redis Token Blacklist

## Spec Reference

ISSUE-4 from `docs/specs/spec-reboot-uam.md`

## What to build

Logout endpoint in `reboot-auth-service`: extract `jti` and remaining TTL from the Bearer token, write `jti` to Redis (`SET blacklist:{jti} EX {remainingTTL}`) via `TokenBlacklist`, revoke the refresh token in DB via `OutboxRelay` (single transaction).

## Acceptance Criteria

- [ ] A valid authenticated request to `POST /auth/logout` returns `204`
- [ ] After logout, a subsequent request using the same access token is rejected with `401`
- [ ] After logout, attempting to use the revoked `refreshToken` at `POST /auth/refresh` returns `401`
- [ ] Logout with an already-expired token returns `401`

## Blocked by

- #3
