# reboot-audit-service & Audit Event Persistence

## Spec Reference

ISSUE-14 from `docs/specs/spec-reboot-uam.md`

## What to build

`reboot-audit-service` Spring Boot service. Flyway schema (`uam_audit`: `audit_events` — `audit_id UUID PK`, `actor_id`, `action`, `resource_type`, `resource_id`, `timestamp`, `payload JSON`, `created_at` — no `updated_at`, no soft delete, append-only by design). DB user restricted to INSERT-only on `audit_events`. `IdempotentConsumer` for `audit.events` → deserialize `AuditEvent` envelope, persist via `AuditEventStore`.

## Acceptance Criteria

- [ ] A valid `AuditEvent` message on `audit.events` results in a persisted audit record (observable via direct DB assertion in integration tests)
- [ ] Receiving the same `auditId` twice persists only one record (idempotent)
- [ ] An `AuditEvent` with missing required fields (`actorId`, `action`, `resourceType`) is routed to `audit.events.DLT`

## Blocked by

- #2
