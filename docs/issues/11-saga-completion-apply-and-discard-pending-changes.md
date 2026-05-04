# Saga Completion — Apply & Discard Pending Changes

## Spec Reference

ISSUE-11 from `docs/specs/spec-reboot-uam.md`

## What to build

`reboot-uam-service` `IdempotentConsumer` for `submission.approved`: apply the `pending_changes` payload to the `customers` table and delete the `pending_changes` row in a single transaction via `CustomerSubmissionService`. Consumer for `submission.rejected`: delete the `pending_changes` row only. Publish a `CustomerUpdated` audit envelope on approval.

## Acceptance Criteria

- [ ] After `submission.approved` is consumed, `GET /customers/{id}` returns the updated data from the pending snapshot
- [ ] After `submission.rejected` is consumed, `GET /customers/{id}` returns the original unchanged data
- [ ] Receiving `submission.approved` twice applies the change only once (idempotent)
- [ ] After approval is applied, no pending submission remains for the same customer
- [ ] Full saga happy path verified end-to-end: Clerk submits → Approver approves → customer record updated (all via Testcontainers Kafka)

## Blocked by

- #10
