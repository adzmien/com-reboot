## Spec Reference

ISSUE-12 from `docs/specs/spec-reboot-uam.md`

## What to build

Implement `AuditEventConsumer` in `reboot-audit-service`. Subscribes to all domain event topics: `reboot.customer.change.submitted`, `reboot.customer.change.approved`, `reboot.customer.change.applied`, `reboot.customer.change.rejected`, `reboot.customer.created`, `reboot.customer.deactivated`, `reboot.user.created`, `reboot.user.deactivated`, `reboot.user.role.changed`, `reboot.admin.kyc.updated`. Writes to `uam_audit.audit_log` with a unique constraint on `event_id` for deduplication. No UPDATE or DELETE operations are permitted on this table. Flyway migration creates the `audit_log` table. Admin-only read endpoint: `GET /audit/{resourceType}/{resourceId}`.

## Acceptance Criteria

- [ ] After a `CustomerChangeApplied` event is published, an audit record exists for that event (observable via `GET /audit/customer/{customerId}`)
- [ ] Receiving the same event twice (same `eventId`) produces exactly one audit record
- [ ] Audit records returned include `actorId`, `eventType`, `correlationId`, and `occurredAt`
- [ ] No audit record can be deleted via any API endpoint (no DELETE on audit routes)

## Blocked by

- #10
