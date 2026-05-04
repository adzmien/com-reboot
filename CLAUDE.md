# CLAUDE.md — reboot-uam

## Project Overview
Reboot-UAM — reusable User Access Management microservices system (company: Reboot). Learning vehicle for AI coding workflow, distributed patterns, Spring Observability, and clean code principles.

---

## Prerequisites
Run `j21` before any command that invokes Java (`./gradlew`, `java`, `javac`, etc.) to set `JAVA_HOME` to Java 21.

---

## Tech Stack
Java 21 · Spring Boot 3.3.x · Gradle (Groovy) · Lombok · MapStruct · Spring Cloud Gateway · OpenFeign · Resilience4j · Kafka · MariaDB (single instance, schema-per-service) · Flyway · Micrometer + OpenTelemetry + Prometheus + Grafana + Tempo · Kubernetes · K8s DNS · ConfigMaps + Secrets

## Local Dev Environment
- **Runtime:** Kubernetes only (kind or minikube). **No Docker Compose** — never create or suggest `docker-compose.yml`.
- All services and infrastructure (MariaDB, Kafka, Redis) are deployed via k8s manifests (`Deployment`, `Service`, `ConfigMap`, `Secret`).
- Local dev parity: `kubectl apply -f k8s/` brings up full stack; each service must reach `/actuator/health = UP`.

---

## Repo Structure
```
/reboot-uam
├── .claude/skills/          grill-me.md, spec.md, to-issues.md, pick-issue.md, doc.md
├── reboot-common-lib/       Shared library
├── reboot-uam-service/      Core UAM service
├── reboot-auth-service/     Authentication service
├── reboot-gateway/          API Gateway
├── docs/issues/             INDEX.md + issue markdown files
└── CLAUDE.md
```

---

## Subprojects & Packages
- `reboot-common-lib` → `com.reboot.uam.lib`
- `reboot-uam-service` → `com.reboot.uam.api`
- `reboot-auth-service` → `com.reboot.auth.api`
- `reboot-gateway` → `com.reboot.uam.gateway`

**Package structure:** Feature-first, then layer. E.g. `com.reboot.uam.api.user.controller`, `com.reboot.uam.api.user.service`, `com.reboot.uam.api.user.model.entity`, `.dto`, `.mapper`, `com.reboot.uam.api.user.repository`, `.exception`.

Service interface + impl pattern (`UserService` + `UserServiceImpl`). Top-level `config/` and `common/` packages per service.

---

## Coding Rules
**Principles:** SOLID · DRY · KISS · YAGNI · GoF patterns where they simplify (don't force).

**Style:**
- Constructor injection only (`@RequiredArgsConstructor`). No `@Autowired`.
- Annotation order: Lombok → Spring → JPA → custom.
- Never expose entities in controllers — always map via MapStruct.
- Bean Validation on request DTOs only, never on entities.
- Repository returns `Optional<T>`. Never return `null` from service layer. Never pass `null` as argument.
- Strictest access modifiers. Max ~20 line methods. No magic values — use constants/enums.
- Javadoc on public interfaces/methods. Inline comments only for *why*, not *what*.

---

## API Response Convention
Standard `ApiResponse<T>` wrapper in `reboot-common-lib` with fields: `success`, `data`, `message`, `error`, `timestamp`.
- `error` object: `code` (flat sequence), `message`, `details[]`
- Validation errors: details with `field` + `message`
- Business rule violations: details with `code` + `message`
- Global `@RestControllerAdvice` per service maps exceptions to this format.

---

## Exception Hierarchy (in `reboot-common-lib`)
`RebootException` (base, carries `code` + `message`) → `ResourceNotFoundException` (404) · `DuplicateResourceException` (409) · `BusinessRuleException` (422) → `BusinessRuleViolationsException` (422, multiple) · `UnauthorizedException` (401) · `ForbiddenException` (403) · `ServiceCommunicationException` (502). Services may extend for domain-specific cases.

---

## Error Codes
Flat sequence: `UAM-001`, `AUTH-001`, `GW-001`. Defined as enums per service. HTTP status determined by exception class.

---

## Auth Model
**Authentication:** JWT access token (~15 min) + refresh token (~7 days, DB-stored). Payload: `sub`, `roles`, `permissions`, `iat`, `exp`.

**Authorization:** RBAC — `User →< UserRole >→ Role →< RolePermission >→ Permission`. Gateway validates JWT, forwards claims via headers. Shared security filter in `reboot-common-lib` populates `SecurityContext`. Method-level: `@PreAuthorize("hasPermission('USER_CREATE')")`.

**Out of scope:** MFA implementation, OAuth2/SSO, permission groups, hierarchical roles.

---

## Database Conventions
- Schemas: `uam_core`, `uam_auth` (all `uam_` prefixed)
- Tables: `snake_case`, plural. Columns: `snake_case`. PK: `id BIGINT AUTO_INCREMENT`.
- FK: `<table_singular>_id`. Index: `idx_<table>_<col>`. Unique: `uk_<table>_<col>`.
- Every table: `created_at`, `created_by`, `updated_at`, `updated_by` (JPA `AuditingEntityListener`).
- Soft delete: `is_deleted` + `deleted_at`. Migrations: Flyway (`V1__description.sql`).

---

## Logging
`@Slf4j` (Lombok). Structured format: `log.info("User created. userId={}, email={}", id, email)`. Correlation via Micrometer trace/span IDs. ERROR = unexpected failures. WARN = recoverable/fallback. INFO = business events. DEBUG = method entry/exit, payloads. Never log passwords, tokens, JWTs, or PII. No repository-level logging.

---

## Testing
- **Unit:** JUnit 5 + Mockito — mandatory for all business logic (`*Test.java`)
- **Integration:** `@SpringBootTest` + Testcontainers (MariaDB) — mandatory for DB code (`*IT.java`)
- **Contract:** MockMvc — mandatory for every REST endpoint
- **Inter-service:** WireMock — for Feign/circuit breaker features

Quality over coverage metrics. No hard percentage target.

---

## Branching & Commits
`main` (always deployable). Feature branches: `feature/<issue#>-<short-desc>`. Conventional Commits: `feat(uam-service): add user CRUD (#001)`. Squash merge. No PR required (solo dev).

---

## Notion MCP
`/spec` and `/doc` write to user-provided Notion page URLs. Never auto-create pages. Issues live in `docs/issues/` only.

---

## General Rules
- When searching or scanning files, only look within the current working directory. Never scan parent directories.

---

## Workflow
5-phase AI coding workflow. Skills in `.claude/skills/`:
1. `/grill-me` → conversation (stress-test the plan)
2. `/spec <notion-url>` → Spec page on Notion
3. `/to-issues` → `INDEX.md` + issue files in `docs/issues/`
4. `/pick-issue` → pick an issue and implement code + tests
5. `/doc <notion-url>` → TDD page on Notion
