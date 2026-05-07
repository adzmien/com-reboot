# CLAUDE.md — reboot-uam

This file is auto-loaded by Claude Code on every turn. Hard Rules are non-negotiable. Conventions below guide code generation.

## Project
Reboot-UAM — reusable User Access Management microservices system. Self-contained Gradle multi-project mono-repo. K8s manifests for shared infra live in this repo too.

**Primary learning goal:** distributed microservices design patterns — **Saga**, **Transactional Outbox**, **Circuit Breaker**, **CQRS**, **API Gateway**, and others — introduced incrementally as the domain genuinely demands them. Secondary goals: Spring observability, clean-code principles, AI-driven workflow.

## HARD RULES
1. **Stay inside this repo only.** Never read, scan, or reference parent directories or any path outside the repo root, unless I explicitly ask.
2. **Run `j21` before any Java command** — `./gradlew`, `java`, `javac`, `gradle`. Every time. No exceptions.
3. **Infra runs in remote K8s; services run from the IDE.** Never containerize, `Dockerfile`, or deploy the application services. Never create `docker-compose.yml` or any Compose-style file. Infra (MariaDB, Kafka, Redis) is deployed to the remote K8s cluster via manifests in `reboot-common-k8s/`; services connect to it via NodePorts.
4. **`application.properties`, never YAML** for Spring config. (K8s manifests in `reboot-common-k8s/` are YAML — that's the only allowed YAML in this repo.)
5. **Never auto-create Notion pages.** `/spec` and `/doc` only write to URLs I provide.
6. **Never log secrets or PII.** No passwords, tokens, JWTs, or full request bodies.
7. **No Docker anywhere — including tests.** There is no local Docker daemon. Never use Testcontainers, Docker Compose, or any container runtime in tests. Integration tests (`*IT.java`) connect to the same remote K8s infra (MariaDB NodePort 30306, Redis NodePort 30379) that services use at runtime. Use `@BeforeEach` / `@AfterEach` to clean up state between tests.

## Local Dev Model
Services run locally from VSCode via `./gradlew bootRun` (after `j21`). They connect to remote K8s infra over NodePorts. There is no local Docker, no Compose, no service deployment.

Infra manifests live in `reboot-common-k8s/`. Apply with `kubectl apply -f reboot-common-k8s/` against the remote cluster context.

`INFRA_HOST` defaults to `100.66.8.44` (Tailscale). `application.properties` references `${INFRA_HOST:100.66.8.44}` so a single env-var swap reroutes the whole stack.

| Component        | Host            | NodePort | Internal | Notes          |
|------------------|-----------------|----------|----------|----------------|
| MariaDB          | `${INFRA_HOST}` | `30306`  | `3306`   |                |
| Kafka broker     | `${INFRA_HOST}` | `30092`  | `9092`   | client traffic |
| Kafka controller | `${INFRA_HOST}` | `30391`  | `9093`   | KRaft          |
| Redis            | `${INFRA_HOST}` | `30379`  | `6379`   |                |

## Tech Stack
Java 21 · Spring Boot 3.3.x · Gradle (Groovy) · Lombok · MapStruct · Spring Cloud Gateway · OpenFeign · Resilience4j · Kafka · MariaDB (single instance, schema-per-service) · Flyway · Micrometer + OpenTelemetry + Prometheus + Grafana + Tempo · Kubernetes (infra only) · ConfigMaps + Secrets

## Repo Structure

    /reboot-uam
    ├── .claude/skills/          grill-me.md, spec.md, to-issues.md, pick-issue.md, doc.md
    ├── reboot-common-lib/       Shared Java library
    ├── reboot-common-k8s/       Kubernetes manifests for shared infra (MariaDB, Kafka, Redis, observability)
    ├── reboot-uam-service/      Core UAM service
    ├── reboot-auth-service/     Authentication service
    ├── reboot-gateway/          API Gateway
    ├── docs/issues/             INDEX.md + issue markdown files
    └── CLAUDE.md

## Subprojects & Packages

**Java (Gradle subprojects):**
- `reboot-common-lib`   → `com.reboot.uam.lib`
- `reboot-uam-service`  → `com.reboot.uam.api`
- `reboot-auth-service` → `com.reboot.auth.api`
- `reboot-gateway`      → `com.reboot.uam.gateway`

**Non-Java:**
- `reboot-common-k8s/` — pure Kubernetes manifests (`Deployment`/`StatefulSet`, `Service`, `ConfigMap`, `Secret`). One subdirectory per infra component. Not a Gradle subproject.

Package layout (Java): feature-first, then layer. E.g. `com.reboot.uam.api.user.{controller,service,repository,mapper,exception}` and `user.model.{entity,dto}`. Service interface + impl pattern (`UserService` + `UserServiceImpl`). Top-level `config/` and `common/` packages per service.

## Coding Conventions

**Principles:** **SOLID** · **DRY** · **KISS** · **YAGNI**. Apply pragmatically — never force a principle when a simpler approach is correct. Prefer the simplest design that solves the current problem; refactor when a second concrete need appears, not before.

- Constructor injection only via `@RequiredArgsConstructor`. Never `@Autowired` on fields.
- Never expose JPA entities at controller boundary. Always map via MapStruct.
- Bean Validation on request DTOs only, never on entities.
- Repositories return `Optional<T>`. Service layer never returns `null` and never accepts `null` arguments.
- Strictest access modifiers. ~20-line methods max. No magic values — use constants/enums.
- DTO naming: suffix with `Request` / `Response` (`RegisterRequest`, `LoginResponse`).
- Event naming: past tense, domain-specific (`ProfileCreated`, `ProfileUpdated`).
- All REST endpoints return `ApiResponse<T>` from `reboot-common-lib`. All thrown business exceptions extend `RebootException` from `reboot-common-lib`. Error code format: `<SERVICE>-<NNN>`.
- Per-service `@RestControllerAdvice` handles exception → `ApiResponse` mapping. Base classes live in `reboot-common-lib`.
- Common-lib extraction: move shared logic into `reboot-common-lib` only when 2+ services need it — not before. (DRY guard.)
- Annotation order: Lombok → Spring → JPA → custom.
- Javadoc on public interfaces/methods. Inline comments only for *why*, not *what*.
- **When uncertain about a convention not stated here, ask before assuming. Do not invent patterns silently.**

## K8s Manifest Conventions (reboot-common-k8s)
- One subdirectory per component: `mariadb/`, `kafka/`, `redis/`, etc. Each contains its own `Deployment`/`StatefulSet`, `Service`, `ConfigMap`, `Secret`.
- Resource names match the directory: `metadata.name: mariadb`, `metadata.name: kafka`, etc.
- All resources in namespace `reboot-infra` (create via `00-namespace.yaml` if absent).
- Secrets: for learning, credentials are committed directly in `*-secret.yaml` files with a `# FOR LEARNING ONLY` comment. Never do this in production.
- NodePorts only for components that services connect to from outside the cluster (the four in the Local Dev Model table). Internal-only components use `ClusterIP`.

## Database
Schemas: `uam_core`, `uam_auth` (all `uam_`-prefixed). Tables: `snake_case`, plural. Columns: `snake_case`. PK: `id BIGINT AUTO_INCREMENT`. FK: `<table_singular>_id`. Index: `idx_<table>_<col>`. Unique: `uk_<table>_<col>`. Audit columns on every table: `created_at`, `created_by`, `updated_at`, `updated_by` (JPA `AuditingEntityListener`). Soft delete: `is_deleted` + `deleted_at`. Migrations: Flyway (`V1__description.sql`).

## Logging
`@Slf4j`. Structured: `log.info("User created. userId={}, email={}", id, email)`. Correlation via Micrometer trace/span IDs. ERROR = unexpected · WARN = recoverable/fallback · INFO = business events · DEBUG = method entry/exit, payloads. No repository-level logging.

## Testing
Unit (`*Test.java`, JUnit 5 + Mockito) · Integration (`*IT.java`, `@SpringBootTest` + remote K8s infra via NodePorts) · Contract (MockMvc per REST endpoint) · Inter-service (WireMock for Feign / circuit breakers). Quality over coverage metrics. No Testcontainers — see Hard Rule #7.

## Design Patterns (general)
Apply GoF / enterprise patterns where they simplify. Name the pattern in a comment when introducing it. Do not force patterns where a simple `if/else` suffices.

| Pattern | Where | Added |
|---------|-------|-------|
|         |       |       |

## Microservice Patterns (learning roadmap)
Patterns are introduced when the domain genuinely requires them — never pre-built. Target patterns to exercise during this project:

- **Saga** (orchestration or choreography) — multi-service business transactions.
- **Transactional Outbox** — reliable event publishing alongside DB writes.
- **Circuit Breaker** + **Retry** + **Bulkhead** + **Time Limiter** — Resilience4j on every inter-service call.
- **CQRS** — read/write separation where read load justifies it.
- **API Gateway** — routing, JWT validation, correlation ID (Spring Cloud Gateway).
- **Idempotency Key** — for retry-safe write endpoints.
- Others as the domain demands (Event Sourcing, Saga Compensation, Inbox, etc.).

| Pattern | Where | Added |
|---------|-------|-------|
|         |       |       |

---
**When you add a new dependency, pattern, or tech, update Tech Stack / Patterns / Repo Structure in this file in the same change.**