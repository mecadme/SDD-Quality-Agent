# Graph Report - .  (2026-07-04)

## Corpus Check
- Corpus is ~37,854 words - fits in a single context window. You may not need a graph.

## Summary
- 121 nodes · 167 edges · 18 communities (14 shown, 4 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 19 edges (avg confidence: 0.87)
- Token cost: 160,000 input · 55,498 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Constitution & Quality Gates|Constitution & Quality Gates]]
- [[_COMMUNITY_Spec Kit Skills & Hooks|Spec Kit Skills & Hooks]]
- [[_COMMUNITY_Appointment Domain & Concurrency|Appointment Domain & Concurrency]]
- [[_COMMUNITY_PowerShell Common Utilities|PowerShell Common Utilities]]
- [[_COMMUNITY_Booking Workflow & Tasks|Booking Workflow & Tasks]]
- [[_COMMUNITY_User Story & Key Entities|User Story & Key Entities]]
- [[_COMMUNITY_Requirements & Success Criteria|Requirements & Success Criteria]]
- [[_COMMUNITY_Spring Boot App Entry Point|Spring Boot App Entry Point]]
- [[_COMMUNITY_Application Tests|Application Tests]]
- [[_COMMUNITY_Plan Technical Context|Plan Technical Context]]
- [[_COMMUNITY_New Feature Script|New Feature Script]]
- [[_COMMUNITY_Agent Context Shell Script|Agent Context Shell Script]]
- [[_COMMUNITY_Complexity Tracking Table|Complexity Tracking Table]]
- [[_COMMUNITY_Parallel Task Marking Convention|Parallel Task Marking Convention]]

## God Nodes (most connected - your core abstractions)
1. `extensions.yml (installed extensions & hooks config)` - 12 edges
2. `Skill: speckit-constitution` - 10 edges
3. `Plan: Constitution Check Table` - 8 edges
4. `Research Decision: Optimistic Locking + DB Unique Constraint for Concurrency Control` - 8 edges
5. `Command: speckit.agent-context.update` - 7 edges
6. `Plan: Project Structure (domain/application/infrastructure/presentation)` - 7 edges
7. `Skill: speckit-checklist` - 6 edges
8. `Domain Model: FranjaHoraria` - 6 edges
9. `Domain Model: Cita` - 6 edges
10. `Skill: speckit-clarify` - 5 edges

## Surprising Connections (you probably didn't know these)
- `Research Decision: Explicit schema.sql/data.sql (ddl-auto=none)` --references--> `Technology & Architecture Standards`  [INFERRED]
  specs/001-online-appointment-booking/research.md → .specify/memory/constitution.md
- `Plan: Project Structure (domain/application/infrastructure/presentation)` --references--> `Plan Template: Project Structure Section`  [INFERRED]
  specs/001-online-appointment-booking/plan.md → .specify/templates/plan-template.md
- `User Story 1: Reserva de cita en línea (autoservicio) P1` --references--> `Spec Template: User Scenarios & Testing Section`  [INFERRED]
  specs/001-online-appointment-booking/spec.md → .specify/templates/spec-template.md
- `Skill: speckit-agent-context-update` --cites--> `Command: speckit.agent-context.update`  [EXTRACTED]
  .claude/skills/speckit-agent-context-update/SKILL.md → .specify/extensions/agent-context/commands/speckit.agent-context.update.md
- `Skill: speckit-specify` --references--> `Command: speckit.agent-context.update`  [INFERRED]
  .claude/skills/speckit-specify/SKILL.md → .specify/extensions/agent-context/commands/speckit.agent-context.update.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Spec Kit Feature Workflow Pipeline** — speckit_specify_skill, speckit_clarify_skill, speckit_plan_skill, speckit_tasks_skill, speckit_analyze_skill, speckit_implement_skill, speckit_converge_skill [INFERRED 0.85]
- **Agent-Context Extension Hook Wiring** — specify_extensions, agent_context_extension, commands_speckit_agent_context_update, specify_extensions_hooks_mechanism [EXTRACTED 1.00]
- **Checklist Gate Before Implementation** — speckit_checklist_skill, speckit_implement_skill, speckit_clarify_skill [INFERRED 0.85]
- **Atomic State Transition Pattern Preventing Double Booking** — 001_online_appointment_booking_research_concurrency_control, 001_online_appointment_booking_data_model_franjahoraria, 001_online_appointment_booking_tasks_franjahorariajparepositoryadapterit, contracts_appointment_booking_openapi_franjahoraria [INFERRED 0.85]
- **API-First Contract-Driven Generation Flow** — memory_constitution_api_first_contract_driven, 001_online_appointment_booking_research_openapi_codegen, contracts_appointment_booking_openapi_reservarcita, 001_online_appointment_booking_tasks_phase1_setup [INFERRED 0.85]
- **BDD Three-Layer Testing Discipline (Unit/Integration/Functional)** — memory_constitution_bdd_testing_discipline, 001_online_appointment_booking_research_bdd_test_structure, 001_online_appointment_booking_tasks_reservarcitausecase_test, 001_online_appointment_booking_tasks_franjahorariajparepositoryadapterit, 001_online_appointment_booking_tasks_citacontrollertest [INFERRED 0.85]

## Communities (18 total, 4 thin omitted)

### Community 0 - "Constitution & Quality Gates"
Cohesion: 0.15
Nodes (19): Plan: Constitution Check Table, Research Decision: JUnit 5 @Nested/@DisplayName for Given-When-Then (no Cucumber), Research Decision: JaCoCo >80% + Checkstyle (google_checks.xml), Research Decision: H2 Storage, Tasks Phase 4: Polish & Cross-Cutting Concerns, Principle IV: API-First with Contract-Driven Generation (NON-NEGOTIABLE), Principle II: BDD Testing Discipline (NON-NEGOTIABLE), Principle I: Clean Architecture (NON-NEGOTIABLE) (+11 more)

### Community 1 - "Spec Kit Skills & Hooks"
Cohesion: 0.25
Nodes (17): agent-context Extension Manifest (extension.yml), Coding Agent Context Extension README, Checklists as 'Unit Tests for Requirements' (English), Command: speckit.agent-context.update, extensions.yml (installed extensions & hooks config), Extension Hooks Mechanism (before_X/after_X), Skill: speckit-agent-context-update, Skill: speckit-analyze (+9 more)

### Community 2 - "Appointment Domain & Concurrency"
Cohesion: 0.18
Nodes (16): Domain Model: Cita, Domain Model: FranjaHoraria, FranjaHorariaRepository.java port (planned), Plan: Project Structure (domain/application/infrastructure/presentation), ReservarCitaUseCase.java (planned), Quickstart Escenario 3: Concurrencia real, Research Decision: Optimistic Locking + DB Unique Constraint for Concurrency Control, Research Decision: Explicit schema.sql/data.sql (ddl-auto=none) (+8 more)

### Community 3 - "PowerShell Common Utilities"
Cohesion: 0.22
Nodes (10): Find-SpecifyRoot(), Format-SpecKitCommand(), Get-CurrentBranch(), Get-FeaturePathsEnv(), Get-InvokeSeparator(), Get-Python3Command(), Get-RepoRoot(), Resolve-SpecifyInitDir() (+2 more)

### Community 4 - "Booking Workflow & Tasks"
Cohesion: 0.22
Nodes (11): Quickstart Escenario 1: Reserva exitosa fuera de horario, Quickstart Escenario 2: Conflicto de disponibilidad en tiempo real, Research Decision: OpenAPI Generator (spring, interfaceOnly), T017: CitaControllerTest.java (functional, MockMvc), Tasks Phase 1: Setup (Shared Infrastructure), Tasks Phase 3: User Story 1 Implementation (MVP), OpenAPI Operation: listarFranjasDisponibles (GET /franjas-horarias), OpenAPI Operation: reservarCita (POST /citas) (+3 more)

### Community 5 - "User Story & Key Entities"
Cohesion: 0.33
Nodes (7): Domain Model: Paciente (external reference), Key Entity: Cita (Reserva), Key Entity: Franja Horaria (Slot), Key Entity: Paciente, User Story 1: Reserva de cita en línea (autoservicio) P1, Workflow Step: specify, Spec Template: User Scenarios & Testing Section

### Community 6 - "Requirements & Success Criteria"
Cohesion: 0.33
Nodes (6): Functional Requirements FR-001..FR-010, Success Criteria SC-001..SC-005, Specification Quality Checklist: Reserva de cita en línea, Checklist Template Structure, Spec Template: Functional Requirements Section, Spec Template: Success Criteria Section

### Community 7 - "Spring Boot App Entry Point"
Cohesion: 0.50
Nodes (3): CitaSaludServiceApplication, SpringBootApplication, String

### Community 8 - "Application Tests"
Cohesion: 0.60
Nodes (3): CitaSaludServiceApplicationTests, SpringBootTest, Test

### Community 10 - "Plan Technical Context"
Cohesion: 0.50
Nodes (4): Plan: Technical Context (Java 25, Spring Boot 4.1.0, H2), CLAUDE.md Speckit Context Pointer, Workflow Step: plan, Plan Template: Technical Context Section

## Knowledge Gaps
- **24 isolated node(s):** `update-agent-context.sh script`, `Skill: speckit-agent-context-update`, `agent-context Extension Manifest (extension.yml)`, `Extension Hooks Mechanism (before_X/after_X)`, `Checklist Template Structure` (+19 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **4 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Research Decision: Optimistic Locking + DB Unique Constraint for Concurrency Control` connect `Appointment Domain & Concurrency` to `Constitution & Quality Gates`, `Plan Technical Context`?**
  _High betweenness centrality (0.082) - this node is a cross-community bridge._
- **Why does `Quickstart Escenario 1: Reserva exitosa fuera de horario` connect `Booking Workflow & Tasks` to `Constitution & Quality Gates`, `Requirements & Success Criteria`?**
  _High betweenness centrality (0.057) - this node is a cross-community bridge._
- **Why does `Plan: Project Structure (domain/application/infrastructure/presentation)` connect `Appointment Domain & Concurrency` to `Booking Workflow & Tasks`?**
  _High betweenness centrality (0.055) - this node is a cross-community bridge._
- **What connects `update-agent-context.sh script`, `Skill: speckit-agent-context-update`, `agent-context Extension Manifest (extension.yml)` to the rest of the system?**
  _26 weakly-connected nodes found - possible documentation gaps or missing edges._