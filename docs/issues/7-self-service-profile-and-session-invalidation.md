# Self-Service Profile & Session Invalidation on Role/Deactivation

## Spec Reference

ISSUE-7 from `docs/specs/spec-reboot-uam.md`

## What to build

`GET /users/me` and `PATCH /users/me` (phone number only) endpoints in `reboot-uam-service`. `reboot-auth-service` `IdempotentConsumer` for `user.deactivated` and `user.role.changed` events: revoke all active refresh tokens for the affected `userId` in a single transaction.

## Acceptance Criteria

- [ ] An authenticated user can retrieve their own profile via `GET /users/me`
- [ ] An authenticated user can update their phone number via `PATCH /users/me`
- [ ] A user cannot update their own role via `PATCH /users/me` (returns `403`)
- [ ] After an admin deactivates a user, that user's refresh token is invalidated (subsequent `POST /auth/refresh` returns `401`)
- [ ] After an admin changes a user's role, that user's refresh token is invalidated
- [ ] Receiving the same `user.deactivated` event twice revokes refresh tokens only once (idempotent)

## Blocked by

- #6
- #3
