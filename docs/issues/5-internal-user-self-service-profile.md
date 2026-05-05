## Spec Reference

ISSUE-6 from `docs/specs/spec-reboot-uam.md`

## What to build

`GET /users/me` returns the authenticated user's profile sourced from the `X-User-Id` header injected by the Gateway. `PATCH /users/me` allows updating phone number only. Attempts to change role via this endpoint return `403` with `UAM-002`. All authenticated roles have access to both endpoints.

## Acceptance Criteria

- [ ] `GET /users/me` returns the authenticated user's profile with correct fields
- [ ] `PATCH /users/me` with `phoneNumber` returns `200` with updated profile
- [ ] `PATCH /users/me` attempting to change role returns `403` with `UAM-002`
- [ ] Request without valid JWT (unauthenticated) returns `401` (Gateway rejects before reaching service)

## Blocked by

- #4
