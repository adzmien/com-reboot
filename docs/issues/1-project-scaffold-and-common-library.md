# Project Scaffold & Common Library

## Spec Reference

ISSUE-1 from `docs/specs/spec-reboot-uam.md`

## What to build

Initialise the Gradle multi-module project with all 5 service modules (`reboot-auth-service`, `reboot-uam-service`, `reboot-workflow-service`, `reboot-audit-service`, `reboot-gateway`) and `reboot-common-lib`. Implement in `reboot-common-lib`: `ApiResponse<T>`, the full exception hierarchy (`RebootException` base → `ResourceNotFoundException`, `DuplicateResourceException`, `BusinessRuleException`, `UnauthorizedException`, `ForbiddenException`, `ServiceCommunicationException`), `@KycSensitive` annotation, `AuditEvent` envelope record, shared JWT security filter (reads gateway-injected headers, populates `SecurityContext`), and `@RestControllerAdvice` base that maps each exception type to `ApiResponse`. Lay module skeletons for `OutboxRelay`, `IdempotentConsumer`, and `TokenBlacklist` in `reboot-common-lib`.

## Acceptance Criteria

- [ ] All 5 service modules compile without errors
- [ ] `reboot-common-lib` is resolvable as a Gradle dependency in all 5 service modules
- [ ] `ApiResponse<T>`, full exception hierarchy, `@KycSensitive`, and `AuditEvent` are available as importable types in every service
- [ ] `@RestControllerAdvice` base correctly maps each exception type to its designated HTTP status and `ApiResponse` error format
- [ ] `OutboxRelay`, `IdempotentConsumer`, and `TokenBlacklist` module skeletons exist with stub interfaces

## Blocked by

None — can start immediately
