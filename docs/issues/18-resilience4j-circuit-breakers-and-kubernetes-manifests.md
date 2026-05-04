# Resilience4j Circuit Breakers & Kubernetes Manifests

## Spec Reference

ISSUE-18 from `docs/specs/spec-reboot-uam.md`

## What to build

Resilience4j circuit breaker on the `auth-service → uam-service` Feign call: configure `slidingWindowSize`, `failureRateThreshold`, and `waitDurationInOpenState`. Fallback: throw `ServiceCommunicationException` (mapped to `502` by `@RestControllerAdvice`). Kubernetes manifests for all 5 services: `Deployment`, `Service`, `ConfigMap`, `Secret`. K8s DNS service discovery between services.

## Acceptance Criteria

- [ ] When `uam-service` is unavailable, `POST /auth/login` returns `502` with `ApiResponse.error.code` set (not a raw 500)
- [ ] After `uam-service` recovers, `POST /auth/login` returns `200` again (circuit half-opens and closes)
- [ ] K8s manifests pass `kubectl apply --dry-run=client` without errors

## Blocked by

- #3
