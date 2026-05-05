## Spec Reference

ISSUE-14 from `docs/specs/spec-reboot-uam.md`

## What to build

Ensure `correlationId` flows end-to-end. The Gateway already generates `X-Correlation-ID` if absent (wired in #3). All services extract it from the `X-Correlation-ID` header and put it into MDC as `correlationId` on every incoming request. Kafka consumers extract `correlationId` from the event payload and put it into MDC before processing. All log lines carry the correlation ID automatically via the SLF4J pattern — no per-call wiring needed after setup.

## Acceptance Criteria

- [ ] A request with a client-provided `X-Correlation-ID` value produces log lines in downstream services containing that same value
- [ ] A request without `X-Correlation-ID` produces a Gateway-generated UUID that appears in all downstream log lines for that request
- [ ] A Kafka event consumed by `audit-service` produces log lines containing the `correlationId` from the event payload

## Blocked by

- #12
