## Spec Reference

ISSUE-11 from `docs/specs/spec-reboot-uam.md`

## What to build

Configure `@RetryableTopic` on `reboot-uam-service`'s `CustomerChangeApproved` consumer: retry-1 (1 min delay), retry-2 (5 min delay), retry-3 (30 min delay). After all retries are exhausted, the message lands on `reboot.customer.change.approved.DLT`. A `KafkaListenerErrorHandler` on the DLT transitions the submission to `FAILED` status in `reboot-workflow-service` by publishing a `SubmissionFailed` event. `FAILED` submissions surface on the admin dashboard. Admin manual retry endpoint: `POST /admin/submissions/{id}/retry` republishes the event to re-trigger application.

## Acceptance Criteria

- [ ] If `uam-service` fails to process `CustomerChangeApproved` on the first attempt, it retries (observable via consumer offset progression in embedded Kafka)
- [ ] After all retry topics are exhausted, the submission status becomes `FAILED` (observable via `GET /submissions/{id}`)
- [ ] `POST /admin/submissions/{id}/retry` on a `FAILED` submission re-triggers application and returns `202`
- [ ] A successfully re-processed retry results in the customer record reflecting the snapshot (observable via `GET /customers/{id}`)

## Blocked by

- #9
