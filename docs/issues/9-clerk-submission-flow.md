# Clerk Submission Flow

## Spec Reference

ISSUE-9 from `docs/specs/spec-reboot-uam.md`

## What to build

Role-aware routing in `PUT /customers/{id}` and `POST /customers` in `reboot-uam-service`: when the caller has the Clerk role, write a `pending_changes` row and publish a `SubmissionCreated` event via `OutboxRelay` in a single transaction (instead of directly updating `customers`). Return `202 Accepted` with `submissionId`. The `SubmissionCreated` event payload must include a full snapshot of all customer fields.

## Acceptance Criteria

- [ ] A Clerk `POST /customers` returns `202` with a `submissionId`
- [ ] A Clerk `PUT /customers/{id}` returns `202` with a `submissionId`
- [ ] The customer record is NOT immediately updated after a Clerk submission (active record unchanged)
- [ ] Admin `GET /customers/{id}` shows the current approved record, not the pending changes
- [ ] **Contract:** `SubmissionCreated` event on `submission.created` is deserializable by `workflow-service` with fields: `submissionId`, `submittedBy`, `submittedAt`, `customerId`, `changeType`, `pendingData` (all customer fields present)

## Blocked by

- #8
