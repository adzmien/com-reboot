# Approval Task Management in workflow-service

## Spec Reference

ISSUE-10 from `docs/specs/spec-reboot-uam.md`

## What to build

`reboot-workflow-service` Flyway schema (`uam_workflow`: `approval_tasks` with `@Version` for optimistic locking, `outbox`, `processed_events`). `IdempotentConsumer` for `submission.created` → create `approval_task` storing the full snapshot from the event. `GET /approvals` (Approver-only) returning the list of PENDING tasks with snapshots. `POST /approvals/{id}/approve` and `POST /approvals/{id}/reject`: separation-of-duties check (`approverId != submittedBy`, `403` if equal), optimistic-lock update via `ApprovalOrchestrator`, publish `SubmissionApproved` or `SubmissionRejected` via `OutboxRelay`.

## Acceptance Criteria

- [ ] After a Clerk submits, `GET /approvals` (as Approver) returns the task with full `pendingData` snapshot
- [ ] An Approver can approve a submission; `POST /approvals/{id}/approve` returns `200`
- [ ] An Approver can reject a submission; `POST /approvals/{id}/reject` returns `200`
- [ ] An Approver attempting to approve their own submission receives `403`
- [ ] Two concurrent approve requests on the same submission: exactly one succeeds, the other receives `409`
- [ ] Receiving `submission.created` twice creates only one `approval_task` (idempotent)
- [ ] A Clerk attempting `POST /approvals/{id}/approve` receives `403`
- [ ] **Contract:** `SubmissionApproved` event on `submission.approved` is deserializable by `uam-service` with fields: `submissionId`, `approvedBy`, `approvedAt`
- [ ] **Contract:** `SubmissionRejected` event on `submission.rejected` is deserializable by `uam-service` with fields: `submissionId`, `rejectedBy`, `rejectedAt`, `reason`

## Blocked by

- #9
