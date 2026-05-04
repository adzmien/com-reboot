# Test Harness & Local Dev Environment

## Spec Reference

ISSUE-2 from `docs/specs/spec-reboot-uam.md`

## What to build

Configure the shared test infrastructure used by every subsequent issue. Singleton Testcontainers configs for MariaDB, Kafka, and Redis (one container instance per test run, not per test). `AbstractIntegrationTest` JUnit 5 base class pre-wired to all three containers. MockMvc base class with pre-configured `ObjectMapper`. Embedded Kafka consumer test utility with a synchronous poll helper. Kubernetes manifests under `k8s/` (Deployments, Services, ConfigMaps, Secrets) for all infrastructure and all 5 services, targeting a local kind/minikube cluster.

## Acceptance Criteria

- [ ] An integration test extending `AbstractIntegrationTest` can write to and read from the MariaDB container
- [ ] An integration test can publish a message to an embedded Kafka topic and assert it is consumable via the synchronous poll helper
- [ ] An integration test can read from and write to the Redis container
- [ ] A sample `@SpringBootTest` in each of the 5 services passes using the shared test harness
- [ ] `kubectl apply -f k8s/` on a local kind/minikube cluster starts all infrastructure; each service reaches `/actuator/health` = `UP`

## Blocked by

- #1
