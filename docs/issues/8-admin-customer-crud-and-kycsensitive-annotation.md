# Admin Customer CRUD & @KycSensitive Annotation

## Spec Reference

ISSUE-8 from `docs/specs/spec-reboot-uam.md`

## What to build

Flyway migrations for `customers` and `pending_changes` tables in `uam_core`. `Customer` and `PendingChange` entities. `@KycSensitive` annotation processor using reflection to diff old vs new `CustomerUpdateRequest` values, identifying which KYC-sensitive fields changed. Admin endpoints (direct write, no approval required): `POST /customers`, `GET /customers/{id}`, `PUT /customers/{id}`, `PATCH /customers/{id}/deactivate`. Publish `CustomerCreated`, `CustomerUpdated`, `CustomerDeactivated` domain events and audit envelopes via `OutboxRelay`.

## Acceptance Criteria

- [ ] Admin `POST /customers` creates a customer record and returns `201` with `customerId`
- [ ] Admin `GET /customers/{id}` returns full customer data including KYC fields
- [ ] Admin `PUT /customers/{id}` updates all fields (including KYC fields) and returns `200`
- [ ] Admin `PATCH /customers/{id}/deactivate` deactivates the record; list endpoint excludes it; `GET /customers/{id}` still returns the deactivated record
- [ ] A Clerk attempting `PUT /customers/{id}` (direct write) receives `403`
- [ ] **Contract:** `CustomerUpdated` event on `customer.updated` is deserializable with fields: `customerId`, `changedFields[]`, `updatedBy`, `timestamp`

## Blocked by

- #6
