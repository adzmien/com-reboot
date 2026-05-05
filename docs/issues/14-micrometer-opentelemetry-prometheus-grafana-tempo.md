## Spec Reference

ISSUE-15 from `docs/specs/spec-reboot-uam.md`

## What to build

Add `micrometer-tracing-bridge-otel` and `opentelemetry-exporter-otlp` to all services. Configure the OTLP exporter to point at Tempo via `${INFRA_HOST}`. Expose `GET /actuator/prometheus` on all services. Add K8s manifests in `reboot-common-k8s/observability/` for Grafana and Tempo (Deployments, Services, ConfigMaps). Verify distributed trace spans link across Gateway and a downstream service for a single request.

## Acceptance Criteria

- [ ] `GET /actuator/prometheus` on each service returns a non-empty response containing standard Spring Boot metrics (`http_server_requests_seconds`, `jvm_memory_used_bytes`)
- [ ] A login request produces a distributed trace in Tempo with spans from both Gateway and `auth-service` sharing the same trace ID

## Blocked by

- #13
