---
name: pick-issue
description: List open issues from `docs/issues/`, brief the chosen one against its source spec and project rules, then hand off to a sub-agent that iterates on the implementation. Tuned to consume `/to-issues` output directly.
---

## Pick Issue

Select the next vertical-slice issue to develop and hand it off to a sub-agent for AFK implementation.

---

## Process

### 1. Discover issues

Source of truth is `docs/issues/INDEX.md`.

1. Read `docs/issues/INDEX.md` and parse the issues table.
2. For each row, open the referenced file in `docs/issues/` and parse the standard sections produced by `/to-issues`:
   - `## Parent` (optional)
   - `## Spec Reference`
   - `## What to build`
   - `## Acceptance Criteria`
   - `## Blocked by`
3. Determine status per issue:
   - Read the body's `Status:` line if present (e.g. `Status: in-progress 2026-05-04`).
   - If absent, status is `open` by default.
   - Reconcile against the `INDEX.md` Status column. If they disagree, **the body wins** — rewrite the table cell to match.
4. Determine **HITL/AFK** per issue from the `Type` column in `INDEX.md`.
5. Determine **blocker resolution**: parse `Blocked by` from `INDEX.md`. For each `#<id>`, look up that issue's status. If all blockers are `done`, the issue is **ready**; otherwise **blocked**.

If `INDEX.md` is missing or has no rows, tell the user:

> No issues found. Run `/to-issues` against an approved spec in `docs/specs/` first.

### 2. Present the list

Show only issues with `status = open`. Sort:

1. **Ready** before **Blocked**.
2. **AFK** before **HITL**.
3. Spec order (`ISSUE-N` ascending) within the same group.

Render as a compact table:

```markdown
| #  | ID  | Title                              | Type | Status | Spec    | Blocked by |
| -- | --- | ---------------------------------- | ---- | ------ | ------- | ---------- |
| 1  | #42 | Saga rollback on payment failure   | AFK  | ready  | ISSUE-3 | —          |
| 2  | #43 | Outbox dispatcher worker           | AFK  | ready  | ISSUE-4 | —          |
| 3  | #44 | Choose JWT vs PASETO for refresh   | HITL | ready  | ISSUE-5 | —          |
```

If there are blocked or in-progress issues, append a one-line summary:

> N blocked / M in-progress hidden by default. Reply `show all` to include them.

Then ask:

> Which issue would you like to develop? Reply with the row number, the tracker ID (e.g. `#42`), or `cancel`.

If `$ARGUMENTS` is non-empty, treat it as a tracker ID or row number and skip the prompt.

### 3. Brief the chosen issue

Once the user picks one:

1. Read the full issue file from `docs/issues/`.
2. Read the referenced spec (`docs/specs/spec-*.md`) and locate `ISSUE-N` in **Part 3**. Pull through the user stories it satisfies (`US-N` from Part 1), the public interface, behaviors to verify, estimate, and risk.
3. Read `CLAUDE.md` at the repo root for project rules.
4. If `What to build` mentions a specific module (e.g. `services/uam-auth`), read that module's local `CLAUDE.md` if present.
5. Summarize back to the user in 5–8 lines:
   - **What** — one-sentence restatement.
   - **Acceptance criteria** — verbatim checklist.
   - **Type / Estimate / Risk** — from the spec's Part 3.
   - **Public interface** — from the spec.
   - **Modules touched** — packages / services likely affected.
   - **Project rules that apply** — e.g. "outbox required for cross-service events", "1 manday = 1 hour".
   - **Blockers** — confirmed clear, or list any still-open ones.

### 4. Ask for max iterations

Ask the user:

> How many max iterations should the sub-agent use? (typical: 3–5)

Wait for a positive integer. If the user replies with `cancel` or anything that isn't a positive integer, stop without changes.

### 5. Set up the branch

1. Run `git checkout -b issue/<id>-<slug>` (slug = the issue filename without the `<id>-` prefix and without `.md`).
2. Append `Status: in-progress <YYYY-MM-DD>` to the issue body, just under the H1.
3. Update the `Status` column for this row in `INDEX.md` to `in-progress`.

### 6. Hand off to sub-agent

Spawn a sub-agent (via Claude Code's Agent tool) with the prompt below. Pass `maxIterations` (`N`) from Step 4.

```
You are implementing a vertical-slice issue in this Spring Boot project.

## Issue
<full issue body>

## Spec excerpt (Part 3 row + relevant Part 1 user stories)
<copied from docs/specs/spec-*.md>

## Project rules
<repo-root CLAUDE.md + relevant module CLAUDE.md>

## Branch
You are already on `issue/<id>-<slug>`.

## Iterate until one of these is true:
1. All acceptance criteria are met AND `./gradlew build` is green.
2. You reach max iterations: <N>.
3. You cannot make progress.

Each iteration: edit → run `./gradlew build` (and any acceptance-criteria tests) → if failures, analyze and fix → repeat. Commit logically along the way.

Conventions:
- Java 21 / Spring Boot 3.
- Multi-project Gradle layout — respect module boundaries.
- 1 manday = 1 hour estimation convention; do not gold-plate beyond the slice.
- Outbox required for cross-service events (see CLAUDE.md).

When you stop, return:
- Commits made (sha + one-line message each).
- Acceptance criteria status (each one: met / unmet, with a short note).
- Files touched.
- Any outstanding follow-ups or decisions deferred.
```

### 7. Report back

When the sub-agent returns, present its summary verbatim to the user. Do NOT auto-flip `Status` to `done` — the user reviews and closes manually.

---

## Rules

- Do NOT modify any file in `docs/issues/` except to update the `Status:` line in the body and the `Status` column in `INDEX.md`.
- Do NOT close or rewrite issue bodies. The body shape is owned by `/to-issues`.
- Do NOT re-derive vertical slices. Granularity is decided upstream by `/spec` and `/to-issues`.
- Respect the project glossary and ADRs referenced from `CLAUDE.md`.
- Java 21 / Spring Boot 3 / Gradle multi-project conventions apply.

---

## Trigger Phrases

- `/pick-issue`
- "what should I work on next"
- "pick an issue"
- "list open issues"
