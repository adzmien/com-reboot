# Admin Internal User CRUD & Lifecycle Events

## Spec Reference

ISSUE-6 from `docs/specs/spec-reboot-uam.md`

## What to build

`reboot-uam-service` Flyway V1 (`uam_core`: `internal_users`, `outbox`, `processed_events`) and Flyway V2 (seed initial admin with bcrypt password from `ADMIN_INITIAL_PASSWORD` env var). `InternalUser` entity, repository, service, and controller. Admin-only endpoints: `POST /users`, `GET /users/{id}`, `PATCH /users/{id}/deactivate`, `PATCH /users/{id}/role`. `@PreAuthorize` RBAC on all endpoints. Publish `UserCreated`, `UserDeactivated`, `UserRoleChanged` events and corresponding audit envelopes via `OutboxRelay`. Expose internal credential endpoint for auth-service Feign calls.

## Acceptance Criteria

- [ ] Admin `POST /users` creates a new internal user; response contains `userId`, `email`, `role`
- [ ] Creating a user with a duplicate email returns `409`
- [ ] Admin `PATCH /users/{id}/deactivate` deactivates a user; subsequent `GET /users/{id}` reflects `status=DEACTIVATED`
- [ ] Admin `PATCH /users/{id}/role` changes the role; subsequent `GET /users/{id}` reflects the new role
- [ ] A Clerk attempting any admin endpoint receives `403`
- [ ] **Contract:** `UserDeactivated` event on `user.deactivated` is deserializable by `auth-service` with fields: `userId`, `timestamp`
- [ ] **Contract:** `UserRoleChanged` event on `user.role.changed` is deserializable by `auth-service` with fields: `userId`, `newRole`, `timestamp`
- [ ] Flyway V2 seed admin is present on service startup

## Blocked by

- #2
