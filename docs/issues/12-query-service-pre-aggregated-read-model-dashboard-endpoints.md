## Spec Reference

ISSUE-13 from `docs/specs/spec-reboot-uam.md`

## What to build

Implement `KafkaEventProjector` in `reboot-query-service`. Subscribes to all domain event topics and updates a pre-aggregated read model in `uam_query`: `dashboard_summary` (active_users, pending_approvals, failed_submissions) and `recent_activity` (ring buffer of last N events). Idempotent projection via `event_id` unique constraint. `DashboardQueryService` exposes: `GET /dashboard/summary`, `GET /dashboard/pending-approvals`, `GET /dashboard/recent-activity`. All three endpoints are Admin-only via Gateway RBAC.

## Acceptance Criteria

- [ ] After a `CustomerChangeSubmitted` event is published, `GET /dashboard/summary` reflects an incremented `pending_approvals` count
- [ ] After a `CustomerChangeApproved` event is published, `GET /dashboard/summary` reflects a decremented `pending_approvals` count
- [ ] `GET /dashboard/recent-activity` includes an entry for a recently processed event with `eventType`, `actorId`, `resourceId`, and `occurredAt`
- [ ] Receiving the same event twice does not double-count metrics
- [ ] Non-admin requests return `403` (Gateway RBAC)

## Blocked by

- #11
