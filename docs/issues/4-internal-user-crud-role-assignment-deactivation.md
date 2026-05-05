## Spec Reference

ISSUE-5 from `docs/specs/spec-reboot-uam.md`

## What to build

Implement `InternalUserService` in `reboot-uam-service`. Flyway migration: `internal_users` table in `uam_core` (id, full_name, email, employee_id, role, is_active, force_password_change, audit columns, soft-delete). Endpoints: create user, get user, list users, change role, deactivate. MapStruct for entity → DTO mapping. `@RestControllerAdvice` for `reboot-uam-service`. Duplicate email returns `409` with `UAM-001`.

## Acceptance Criteria

- [ ] `POST /users` with valid payload returns `201` with created user (no password in response)
- [ ] `POST /users` with duplicate email returns `409` with `UAM-001` error code
- [ ] `DELETE /users/{id}` returns `200`; the user can no longer log in (`auth-service` returns `401`)
- [ ] `PATCH /users/{id}/role` with a valid role returns `200`; subsequent requests by that user are evaluated against the new role
- [ ] Non-admin requests to any of these endpoints return `403` (enforced at Gateway)

## Blocked by

- #3
