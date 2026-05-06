# Issue #1 — Gradle setup, common-lib, test harness, scaffolds, and Flyway

Status: in-progress 2026-05-06

## Spec Reference

ISSUE-1 and ISSUE-2 from `docs/specs/spec-reboot-uam.md`

## What to build

Configure the Gradle multi-project build with all 6 subprojects and `reboot-common-lib`. Implement `ApiResponse<T>`, `RebootException` hierarchy, service error code constants, and a `@RestControllerAdvice` base class in common-lib. Create a shared `testFixtures` source set with Testcontainers (MariaDB), embedded Kafka, embedded Redis, and base `@SpringBootTest` classes that all integration tests extend. Scaffold all 6 Spring Boot services (`reboot-auth-service`, `reboot-uam-service`, `reboot-gateway`, `reboot-workflow-service`, `reboot-audit-service`, `reboot-query-service`) with `application.properties` (using `${INFRA_HOST:100.66.8.44}` for all infra endpoints), health endpoints, and Flyway. Create baseline migrations for all schemas (`uam_auth`, `uam_core`, `uam_workflow`, `uam_audit`, `uam_query`) with audit columns and soft-delete on all tables. Seed the default admin account in `uam_auth` via Flyway with a bcrypt-hashed default password and `force_password_change = true`.

## Acceptance Criteria

- [ ] All 6 subprojects compile with `./gradlew build`
- [ ] Shared test fixtures are importable from any subproject's test scope
- [ ] A service that throws a `RebootException` subclass returns `ApiResponse` with the correct error code and HTTP status
- [ ] A test extending `BaseIntegrationTest` can write to and read from a Testcontainers MariaDB instance without additional setup
- [ ] A test extending `EmbeddedKafkaTest` can publish and consume a Kafka message without additional setup
- [ ] Each service returns `200 {"status": "UP"}` on `GET /actuator/health` when infra is reachable
- [ ] On first startup, Flyway creates all schemas and tables without error
- [ ] On subsequent startups, Flyway applies no migrations (idempotent)
- [ ] The default admin account exists in `uam_auth.internal_users` with `force_password_change = true`

## Blocked by

None — can start immediately
