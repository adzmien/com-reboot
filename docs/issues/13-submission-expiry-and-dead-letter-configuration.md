# Submission Expiry & Dead-Letter Configuration

## Spec Reference

ISSUE-13 from `docs/specs/spec-reboot-uam.md`

## What to build

`reboot-workflow-service` `SubmissionExpiryScheduler` (`@Scheduled`, configurable interval): query `approval_tasks` where `status=PENDING` and `created_at < now() - threshold`; update status to `EXPIRED`, publish `SubmissionExpired` via `OutboxRelay`. `reboot-uam-service` `IdempotentConsumer` for `submission.expired` → delete `pending_changes` row via `CustomerSubmissionService`. Dead-letter topic configuration across all consumers (after N retries → `{topic}.DLT`). Exponential backoff retry configuration.

## Acceptance Criteria

- [ ] A pending submission older than the configured threshold is no longer in the pending list after the scheduler runs
- [ ] After `submission.expired` is consumed, `GET /customers/{id}` returns the original unchanged customer record
- [ ] Receiving `submission.expired` twice discards the pending change only once (idempotent)
- [ ] A consumer that throws a non-retryable exception routes the message to the `.DLT` topic after max retries
- [ ] **Contract:** `SubmissionExpired` event on `submission.expired` is deserializable by `uam-service` with fields: `submissionId`, `customerId`, `expiredAt`

## Blocked by

- #11
