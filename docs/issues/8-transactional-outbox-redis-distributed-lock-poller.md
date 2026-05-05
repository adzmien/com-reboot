## Spec Reference

ISSUE-9 from `docs/specs/spec-reboot-uam.md`

## What to build

Implement `OutboxPublisher` in `reboot-uam-service` and `reboot-workflow-service`. `outbox_events` table in both `uam_core` and `uam_workflow`: `id`, `topic`, `payload` (JSON), `status` (PENDING/PUBLISHED), `event_id` (UUID), `created_at`. A `@Scheduled` poller uses `SELECT FOR UPDATE SKIP LOCKED` to claim rows. It acquires a Redis distributed lock (`RedisLockRegistry`) before polling — only the lock holder publishes. Marks rows `PUBLISHED` after a successful Kafka send. Lock TTL is 30 seconds with heartbeat renewal.

## Acceptance Criteria

- [ ] An event written to the outbox table is eventually published to the correct Kafka topic (verifiable via embedded Kafka consumer)
- [ ] If two instances attempt to poll simultaneously, each event is published exactly once (no duplicates on Kafka)
- [ ] If the service crashes after writing to the outbox but before publishing, the event is published on the next poll cycle
- [ ] **Contract:** A `CustomerChangeSubmitted` event published to `reboot.customer.change.submitted` is deserializable by `workflow-service` with fields: `submissionId`, `customerId`, `snapshot`, `submittedBy`, `eventId`, `correlationId`

## Blocked by

- #7
