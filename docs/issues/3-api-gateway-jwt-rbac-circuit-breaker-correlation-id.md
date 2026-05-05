## Spec Reference

ISSUE-4 from `docs/specs/spec-reboot-uam.md`

## What to build

Implement `GatewaySecurityFilter` in `reboot-gateway`. Validate JWT signature and expiry on every request; extract `userId`, `role`, and `email` claims and forward as `X-User-Id`, `X-User-Role`, `X-User-Email` headers to downstream services. Generate or pass through `X-Correlation-ID` (UUID if absent). Enforce coarse-grained RBAC at route level (e.g. `POST /submissions/{id}/approve` → `APPROVER` only). Configure Resilience4j `CircuitBreaker`, `Bulkhead`, and `TimeLimiter` on each downstream route.

## Acceptance Criteria

- [ ] Request without `Authorization` header → `401`
- [ ] Request with expired JWT → `401`
- [ ] Request with valid JWT but wrong role for the route → `403`
- [ ] Request with valid JWT and correct role → forwarded to downstream with `X-User-Id`, `X-User-Role`, `X-Correlation-ID` headers present
- [ ] `X-Correlation-ID` from client is preserved; absent header → Gateway generates a UUID
- [ ] When a downstream service is unavailable, Circuit Breaker trips after threshold and returns `503` instead of hanging

## Blocked by

- #2
