# Distributed Tracing & Structured Logging

## Spec Reference

ISSUE-17 from `docs/specs/spec-reboot-uam.md`

## What to build

Add Micrometer + OpenTelemetry to all 5 services. Configure trace/span propagation via HTTP headers (`traceparent`) and Kafka record headers. MDC enrichment: `traceId` and `spanId` present in all log statements. Prometheus scrape endpoint (`/actuator/prometheus`) on each service. Grafana + Tempo datasource configuration in Docker Compose.

## Acceptance Criteria

- [ ] A login request generates a trace visible across gateway → auth-service → uam-service (same `traceId` in logs of all three services)
- [ ] `/actuator/prometheus` on each service returns HTTP request counters
- [ ] `/actuator/health` returns `UP` for each service
- [ ] Trace propagation verified manually via Docker Compose + Grafana/Tempo local stack

## Blocked by

- #12
- #13
- #15
- #16
