## Spec Reference

ISSUE-10 from `docs/specs/spec-reboot-uam.md`

## What to build

Wire the full Saga happy path end-to-end. In `reboot-uam-service`: `PATCH /customers/{id}/submissions` writes a pending change and outbox row in a single transaction. In `reboot-workflow-service`: `SubmissionService` consumes `CustomerChangeSubmitted`, creates an approval task with the full customer snapshot stored as JSON in `uam_workflow.submissions`. Approve endpoint (`POST /submissions/{id}/approve`) runs `SeparationOfDutiesGuard` (approverId ≠ submittedBy) and publishes `CustomerChangeApproved` via the outbox. `reboot-uam-service` consumes `CustomerChangeApproved`, applies the snapshot, clears `has_pending_submission`, and emits `CustomerChangeApplied`. Reject endpoint publishes `CustomerChangeRejected`; `reboot-uam-service` clears the flag. All event payloads include `eventId` (UUID) and `correlationId`.

## Acceptance Criteria

- [ ] `PATCH /customers/{id}/submissions` returns `202 Accepted` with `submissionId`; `has_pending_submission` is `true` (observable via `GET /customers/{id}`)
- [ ] `POST /submissions/{id}/approve` by an approver who did not submit returns `200`; customer record reflects the snapshot values (observable via `GET /customers/{id}`)
- [ ] `POST /submissions/{id}/approve` by the same user who submitted returns `403` with `WORKFLOW-001`
- [ ] `POST /submissions/{id}/reject` returns `200`; `has_pending_submission` is `false`; customer record is unchanged
- [ ] A second `PATCH /customers/{id}/submissions` while `has_pending_submission = true` returns `409`
- [ ] **Contract (CustomerChangeSubmitted):** event on `reboot.customer.change.submitted` is deserializable by `workflow-service` with fields: `submissionId`, `customerId`, `snapshot`, `submittedBy`, `eventId`, `correlationId`
- [ ] **Contract (CustomerChangeApproved):** event on `reboot.customer.change.approved` is deserializable by `uam-service` with fields: `submissionId`, `customerId`, `snapshot`, `approvedBy`, `eventId`, `correlationId`

## Blocked by

- #8
