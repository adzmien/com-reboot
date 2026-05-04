# Audit Envelope Publishing Across All Producing Services

## Spec Reference

ISSUE-15 from `docs/specs/spec-reboot-uam.md`

## What to build

Finalise the `AuditEvent` record in `reboot-common-lib`: `{auditId UUID, actorId, action, resourceType, resourceId, timestamp, payload}`. Extend `OutboxRelay` in all producing services to co-publish an `AuditEvent` to `audit.events` alongside every domain event within the same transaction. Covers all 11 action types: `UserCreated`, `UserDeactivated`, `UserRoleChanged`, `CustomerCreated`, `CustomerUpdated`, `CustomerDeactivated`, `SubmissionCreated`, `SubmissionApproved`, `SubmissionRejected`, `SubmissionExpired`, `AccountLocked`.

## Acceptance Criteria

- [ ] Admin `POST /users` results in a `UserCreated` audit event on `audit.events` with `actorId`, `action=USER_CREATED`, `resourceType=INTERNAL_USER`, `resourceId=userId`
- [ ] An Approver approving a submission results in a `SubmissionApproved` audit event on `audit.events`
- [ ] Each audit event carries a unique `auditId` across multiple actions
- [ ] End-to-end verified for at least one action per producing service: action performed → audit event received by `audit-service` → record persisted

## Blocked by

- #14
- #6
- #8
- #10
