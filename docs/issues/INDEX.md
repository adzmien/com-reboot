# Issues Index

| ID  | Title                                                   | Type | Status | Spec     | Blocked by         | File                                                              |
| --- | ------------------------------------------------------- | ---- | ------ | -------- | ------------------ | ----------------------------------------------------------------- |
| #1  | Project Scaffold & Common Library                       | AFK  | done        | ISSUE-1  | —                  | `1-project-scaffold-and-common-library.md`                        |
| #2  | Test Harness & Local Dev Environment                    | AFK  | open   | ISSUE-2  | #1                 | `2-test-harness-and-local-dev-environment.md`                     |
| #3  | Login, JWT Issuance & Token Refresh                     | AFK  | open   | ISSUE-3  | #2                 | `3-login-jwt-issuance-and-token-refresh.md`                       |
| #4  | Logout & Redis Token Blacklist                          | AFK  | open   | ISSUE-4  | #3                 | `4-logout-and-redis-token-blacklist.md`                           |
| #5  | Gateway Authentication Filter                           | AFK  | open   | ISSUE-5  | #3, #4             | `5-gateway-authentication-filter.md`                              |
| #6  | Admin Internal User CRUD & Lifecycle Events             | AFK  | open   | ISSUE-6  | #2                 | `6-admin-internal-user-crud-and-lifecycle-events.md`              |
| #7  | Self-Service Profile & Session Invalidation             | AFK  | open   | ISSUE-7  | #6, #3             | `7-self-service-profile-and-session-invalidation.md`              |
| #8  | Admin Customer CRUD & @KycSensitive Annotation          | AFK  | open   | ISSUE-8  | #6                 | `8-admin-customer-crud-and-kycsensitive-annotation.md`            |
| #9  | Clerk Submission Flow                                   | AFK  | open   | ISSUE-9  | #8                 | `9-clerk-submission-flow.md`                                      |
| #10 | Approval Task Management in workflow-service            | AFK  | open   | ISSUE-10 | #9                 | `10-approval-task-management-in-workflow-service.md`              |
| #11 | Saga Completion — Apply & Discard Pending Changes       | AFK  | open   | ISSUE-11 | #10                | `11-saga-completion-apply-and-discard-pending-changes.md`         |
| #12 | Redis-Backed Account Lockout                            | AFK  | open   | ISSUE-12 | #3, #6             | `12-redis-backed-account-lockout.md`                              |
| #13 | Submission Expiry & Dead-Letter Configuration           | AFK  | open   | ISSUE-13 | #11                | `13-submission-expiry-and-dead-letter-configuration.md`           |
| #14 | reboot-audit-service & Audit Event Persistence          | AFK  | open   | ISSUE-14 | #2                 | `14-reboot-audit-service-and-audit-event-persistence.md`          |
| #15 | Audit Envelope Publishing Across All Producing Services | AFK  | open   | ISSUE-15 | #14, #6, #8, #10   | `15-audit-envelope-publishing-across-all-producing-services.md`   |
| #16 | CQRS Read Model & Dashboard API                         | AFK  | open   | ISSUE-16 | #11, #6            | `16-cqrs-read-model-and-dashboard-api.md`                         |
| #17 | Distributed Tracing & Structured Logging                | AFK  | open   | ISSUE-17 | #12, #13, #15, #16 | `17-distributed-tracing-and-structured-logging.md`                |
| #18 | Resilience4j Circuit Breakers & Kubernetes Manifests    | AFK  | open   | ISSUE-18 | #3                 | `18-resilience4j-circuit-breakers-and-kubernetes-manifests.md`    |
