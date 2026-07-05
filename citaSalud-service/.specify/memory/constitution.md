<!--
Sync Impact Report
==================
Version change: [TEMPLATE] → 1.0.0 (initial ratification)

Modified principles: N/A (first concrete version; template placeholders replaced)

Added sections:
- Core Principles I–VII (Clean Architecture, BDD Testing Discipline, SOLID/Clean Code,
  API-First with OpenAPI Generator, Lombok for Boilerplate Reduction, Quality Gates via
  JaCoCo, Static Analysis & Production Readiness)
- Technology & Architecture Standards (Section 2)
- Development Workflow & Quality Gates (Section 3)
- Governance

Removed sections: N/A (template scaffolding only)

Templates requiring updates:
- ✅ .specify/templates/plan-template.md — generic "Constitution Check" gate already
  defers to this file; no hardcoded principle text to reconcile.
- ✅ .specify/templates/spec-template.md — generic, technology-agnostic; no changes needed.
- ✅ .specify/templates/tasks-template.md — generic phase structure; already supports
  test-first ordering and per-layer task organization; no changes needed.
- ✅ .specify/templates/commands/*.md — no directory of command files found under
  .specify/templates/commands in this project; nothing to reconcile.
- ⚠ README.md — no README.md present in repository root (only CLAUDE.md, HELP.md).
  Recommend adding a README that references this constitution once written.

Follow-up TODOs:
- TODO(RATIFICATION_DATE): Original ratification date assumed to be the date this
  constitution was first authored (2026-07-04) since no prior adoption record exists.
-->

# citaSalud-service Constitution

## Core Principles

### I. Clean Architecture (NON-NEGOTIABLE)

The codebase MUST be organized into four explicit, independently compilable layers:
**Domain**, **Application**, **Infrastructure**, and **Presentation**.

- Domain layer MUST contain only business entities, value objects, and domain rules.
  It MUST NOT depend on any framework, persistence technology, or web technology
  (no Spring, JPA, or HTTP annotations in domain classes).
- Application layer MUST contain use cases/services that orchestrate domain logic
  behind interfaces (ports); it MUST depend only on the Domain layer and on
  abstractions it defines, never on Infrastructure or Presentation concretions.
- Infrastructure layer MUST implement the ports defined by the Application layer
  (repositories, external clients, persistence adapters) and MUST depend inward only
  (Application, Domain), never the reverse.
- Presentation layer (controllers, DTOs, mappers) MUST depend on the Application
  layer's use cases and MUST NOT contain business logic.
- Dependencies MUST always point inward (Presentation → Application → Domain,
  Infrastructure → Application/Domain). Any dependency pointing outward from Domain
  or Application is a constitution violation and MUST be rejected in review.

**Rationale**: Framework-independent business logic is the only way to keep the
domain testable in isolation, swappable at the infrastructure edges, and resilient
to framework upgrades or replacements over the life of the service.

### II. BDD Testing Discipline (NON-NEGOTIABLE)

Every feature MUST ship with three test layers, each written using the
**Given-When-Then** structure:

- **Unit tests**: exercise Domain and Application logic in isolation, with
  Infrastructure and Presentation dependencies replaced by test doubles.
- **Integration tests**: exercise Infrastructure adapters (repositories, external
  clients) against real or containerized dependencies (e.g., real database via
  Testcontainers/H2), verifying the adapter honors its port contract.
- **Functional tests**: exercise the Presentation layer end-to-end (e.g., via
  `MockMvc`/`WebTestClient` or full application context), verifying the
  user-facing behavior described in the feature's acceptance scenarios.

Tests MUST be written before or alongside implementation for the corresponding
slice, and MUST fail first to prove they exercise real behavior (red-green
discipline). A feature without all three layers is incomplete and MUST NOT be
merged.

**Rationale**: BDD's Given-When-Then format keeps tests traceable to acceptance
scenarios in the spec, while the three-layer split ensures both isolated logic
and real integration/user-facing behavior are verified.

### III. SOLID, Clean Code & Design Discipline

All code MUST adhere to SOLID, DRY, and YAGNI:

- **Single Responsibility**: one reason to change per class/module.
- **Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion**:
  depend on abstractions (ports/interfaces), not concretions; high-level modules
  (Application) MUST NOT depend on low-level modules (Infrastructure) directly.
- **DRY**: no duplicated business rules or logic across layers; shared behavior
  MUST be extracted to a single, well-named owner.
- **YAGNI**: do not build abstractions, configuration options, or extension points
  for hypothetical future needs — only for requirements already specified.
- **Low coupling, high cohesion**: modules MUST expose minimal, intention-revealing
  interfaces and hide implementation detail.
- **Meaningful naming**: names MUST reveal intent without requiring a comment;
  abbreviations and generic names (`data`, `helper`, `manager`, `util`) are
  disallowed unless the domain itself uses that term ubiquitously.
- **Consistent coding standards**: the project's formatter/linter configuration is
  the single source of truth for style; no ad hoc formatting deviations.

**Rationale**: These are the load-bearing practices that keep a multi-layer,
long-lived service maintainable as the team and codebase grow.

### IV. API-First with Contract-Driven Generation (NON-NEGOTIABLE)

Every HTTP API MUST be designed API-first:

- The API contract MUST be authored as a versioned OpenAPI document
  (e.g., `openapi/citasalud-v1.yaml`) *before* any controller or client code is
  written. Contract changes are proposed and reviewed as diffs to this document.
- The OpenAPI version MUST be bumped following semantic versioning whenever the
  contract changes (MAJOR for breaking changes, MINOR for backward-compatible
  additions, PATCH for clarifications).
- Server-side interfaces/DTOs and any client SDKs MUST be generated from the
  OpenAPI contract using **OpenAPI Generator** as part of the build — hand-written
  controller interfaces or request/response DTOs that duplicate the contract are
  disallowed. Business logic MUST live in generated interface implementations, not
  in generated code itself.
- A build MUST fail if generated code and the checked-in OpenAPI contract have
  drifted (i.e., generation step is wired into the build, not a manual side step).

**Rationale**: Designing the contract first prevents implementation details from
leaking into the API shape, keeps client and server in lockstep, and makes
breaking changes visible and versioned rather than accidental.

### V. Lombok for Boilerplate Reduction

Lombok MUST be used to eliminate repetitive, non-business boilerplate:
`@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`, `@NoArgsConstructor`,
and `@AllArgsConstructor` are the preferred way to generate accessors and
constructors on Domain, Application, and DTO classes.

- Lombok MUST NOT be used to hide or generate business logic — no `@Data` used as
  a substitute for deliberate API design on Domain entities where invariants must
  be enforced in explicit constructors/factory methods.
- Domain entities with invariants MUST prefer explicit constructors or static
  factory methods (optionally combined with `@Builder`) over an open
  `@AllArgsConstructor` + public setters that would allow invalid state.

**Rationale**: Lombok removes mechanical noise so reviews focus on behavior, but
only where the generated code is purely structural — never where correctness
depends on validation logic.

### VI. Quality Gates via Automated Coverage Enforcement (NON-NEGOTIABLE)

JaCoCo MUST be configured as an enforced build gate, not just a report:

- Per-class line/branch coverage MUST be **> 80%**.
- Global (project-wide) coverage MUST be **≥ 80%**.
- The build MUST fail (`jacocoTestCoverageVerification` or equivalent wired into
  `check`/CI) when either threshold is not met — coverage is a gate, not a
  dashboard metric to review later.
- Coverage MUST be earned through the tests required by Principle II (unit,
  integration, functional), not through incidental exercising of code by
  unrelated tests.

**Rationale**: A gate that can be silently ignored is not a gate. Tying coverage
enforcement to the build ensures regressions in test discipline are caught before
merge, not discovered in retrospect.

### VII. Static Analysis & Production Readiness

All code MUST pass static analysis before merge and MUST be production-ready:

- Static analysis tooling (e.g., Checkstyle/SpotBugs/PMD or equivalent configured
  for this project) MUST run as part of the build, with violations treated as
  build failures for new/modified code.
- Code MUST be documented where intent is non-obvious (public API Javadoc on
  Application-layer interfaces and Presentation-layer controllers at minimum);
  implementation comments are reserved for non-obvious rationale, not restating
  code.
- Code MUST be maintainable: no dead code, no commented-out blocks, no
  TODO-without-ticket left in merged code.
- "Production-ready" means: structured error handling at Presentation boundaries,
  no secrets or environment-specific values hardcoded, and logging sufficient to
  diagnose failures without exposing sensitive data.

**Rationale**: Static analysis and documentation requirements catch defects and
knowledge gaps that tests alone do not, and keep the service operable by anyone
on the team, not just its original author.

## Technology & Architecture Standards

- **Stack**: Java (toolchain per `build.gradle`), Spring Boot, Spring Data JPA,
  Gradle build system. Any change to this baseline stack is an architectural
  decision and MUST be reflected in `plan.md`'s Technical Context and justified
  against Principle I (framework independence of Domain/Application layers).
- **Persistence**: Infrastructure-layer repositories MUST implement
  Application-layer port interfaces; JPA entities are Infrastructure-layer
  concerns and MUST be mapped to/from Domain entities at the Infrastructure
  boundary — JPA annotations MUST NOT appear on Domain entities.
- **API contracts**: OpenAPI documents live under a dedicated, versioned location
  (e.g., `openapi/`) and are the single source of truth for request/response
  shapes per Principle IV.
- **Dependency direction**: enforced via package structure
  (`domain`, `application`, `infrastructure`, `presentation` or equivalent) such
  that a dependency-direction violation is visible from package imports alone.

## Development Workflow & Quality Gates

- **Definition of Done** for any task/feature: Clean Architecture boundaries
  respected (Principle I), unit + integration + functional tests present and
  green (Principle II), SOLID/Clean Code review passed (Principle III), OpenAPI
  contract updated and code (re)generated where applicable (Principle IV),
  JaCoCo thresholds met (Principle VI), static analysis clean (Principle VII).
- **Code review** MUST explicitly check for constitution compliance; a reviewer
  MUST reject a PR that violates a NON-NEGOTIABLE principle (I, II, IV, VI)
  regardless of time pressure, unless an explicit, documented exception is
  recorded in the relevant `plan.md` Complexity Tracking table.
- **Build pipeline** MUST run, in order: compilation → OpenAPI contract
  generation check → static analysis → unit/integration/functional tests →
  JaCoCo verification. A failure at any stage MUST stop the pipeline.
- **Complexity/exceptions**: any deviation from a Core Principle MUST be recorded
  with rationale in the feature's `plan.md` Complexity Tracking section before
  implementation proceeds.

## Governance

This constitution supersedes all other project practices, conventions, and prior
undocumented agreements. Where a conflict exists between this document and any
other guidance (README, code comments, ad hoc team convention), this constitution
wins until formally amended.

- **Amendment procedure**: Amendments are proposed as a diff to this file,
  including a Sync Impact Report (version bump rationale, affected templates).
  Amendments MUST be reviewed and explicitly approved before merge; NON-NEGOTIABLE
  principles (I, II, IV, VI) require unanimous maintainer sign-off to change,
  weaken, or remove.
- **Versioning policy**: Semantic versioning applies to this constitution itself:
  MAJOR for backward-incompatible principle removal/redefinition, MINOR for a new
  principle or materially expanded guidance, PATCH for clarifications/typos.
- **Compliance review**: Every `/speckit-plan` run MUST re-evaluate the
  Constitution Check gate after Phase 1 design; every PR/code review MUST verify
  compliance with the Definition of Done above. Repeated or systemic violations
  MUST trigger a review of whether the constitution needs amendment or the team
  needs process reinforcement.
- Use `CLAUDE.md` (or the equivalent agent context file) for day-to-day runtime
  development guidance that operationalizes these principles; that file MUST NOT
  contradict this constitution.

**Version**: 1.0.0 | **Ratified**: 2026-07-04 | **Last Amended**: 2026-07-04
