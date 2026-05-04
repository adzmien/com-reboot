# CQRS Read Model & Dashboard API

## Spec Reference

ISSUE-16 from `docs/specs/spec-reboot-uam.md`

## What to build

Flyway migration in `reboot-uam-service` for `dashboard_summary` singleton table (`active_users_count`, `pending_approvals_count`, `rejected_submissions_count`, `expired_submissions_count`, `last_updated_at`). `IdempotentConsumer` handlers in `DashboardProjection` update counts atomically on: `UserCreated`, `UserDeactivated`, `SubmissionCreated`, `SubmissionApproved`, `SubmissionRejected`, `SubmissionExpired`. Admin-only `GET /dashboard` endpoint queries the singleton row.

## Acceptance Criteria

- [ ] Admin `GET /dashboard` returns `200` with `activeUsersCount`, `pendingApprovalsCount`, `rejectedSubmissionsCount`
- [ ] After a Clerk submits a record, `GET /dashboard` eventually reflects `pendingApprovalsCount` incremented by 1
- [ ] After an Approver approves a submission, `GET /dashboard` eventually reflects `pendingApprovalsCount` decremented by 1
- [ ] A Clerk attempting `GET /dashboard` receives `403`
- [ ] Receiving the same `SubmissionCreated` event twice increments `pendingApprovalsCount` only once (idempotent)

## Blocked by

- #11
- #6
