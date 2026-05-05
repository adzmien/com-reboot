## Spec Reference

ISSUE-7 from `docs/specs/spec-reboot-uam.md`

## What to build

Implement `CustomerRecordService` in `reboot-uam-service`. Flyway migration: `customer_records` table in `uam_core` (id, full_name, email, phone, dob, national_id, address, nationality, occupation, has_pending_submission, `@Version` column, audit columns, soft-delete). Implement `IdempotencyGuard` (Redis, `Idempotency-Key` header required on create/update, 24h TTL). Clerk create and update endpoints write directly to the customer record — the Outbox and Saga event chain is wired in a later issue.

## Acceptance Criteria

- [ ] `POST /customers` with valid payload and `Idempotency-Key` header returns `201` with customer ID
- [ ] `POST /customers` with the same `Idempotency-Key` returns `201` with the same customer ID — no duplicate created
- [ ] `POST /customers` without `Idempotency-Key` header returns `400` with `UAM-003`
- [ ] `PATCH /customers/{id}` when `has_pending_submission = true` returns `409` with `UAM-004`
- [ ] Non-Clerk requests to Clerk endpoints return `403` (Gateway RBAC)

## Blocked by

- #5
