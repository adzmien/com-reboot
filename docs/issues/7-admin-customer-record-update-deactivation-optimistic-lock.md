## Spec Reference

ISSUE-8 from `docs/specs/spec-reboot-uam.md`

## What to build

Admin-only endpoints in `CustomerRecordService`. `PUT /admin/customers/{id}` — full update, bypasses `has_pending_submission` check, no approval required. If KYC fields (`national_id`, `dob`, `address`, `nationality`, `occupation`) are changed, write an `AdminKycFieldUpdated` event to the `outbox_events` table (wired to Kafka in issue #8). `DELETE /admin/customers/{id}` — soft delete. `@Version` on the `customer_records` entity — concurrent admin updates to the same record return `409` with `UAM-005`.

## Acceptance Criteria

- [ ] `PUT /admin/customers/{id}` returns `200` with updated record regardless of `has_pending_submission` state
- [ ] `DELETE /admin/customers/{id}` returns `200`; the record no longer appears in the active customer list
- [ ] Concurrent updates from two admins to the same record — one succeeds (`200`), the other returns `409` with `UAM-005`
- [ ] Non-admin requests to admin endpoints return `403` (Gateway RBAC)

## Blocked by

- #6
