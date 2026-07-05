---

description: "Task list template for feature implementation"
---

# Tasks: Reserva de cita en línea (autoservicio)

**Input**: Design documents from `/specs/001-online-appointment-booking/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/appointment-booking.openapi.yaml, quickstart.md

**Tests**: Incluidos como tareas obligatorias (no opcionales) porque la
constitución del proyecto (Principio II, NON-NEGOTIABLE) exige los tres
niveles — unit, integración y funcional — con estructura Given-When-Then
para toda feature.

**Organization**: El spec de esta feature define una única historia de
usuario (US1, P1), por lo que casi todo el trabajo de negocio vive en la
Fase 3; Setup y Foundational cubren la habilitación inicial del repositorio
(Principios IV, VI, VII) y el modelo/puertos compartidos.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1)
- Include exact file paths in descriptions

## Path Conventions

Proyecto único (backend Spring Boot). Fuente en
`src/main/java/org/ups/citasalud/`, tests en
`src/test/java/org/ups/citasalud/{unit,integration,functional}/`, contrato
OpenAPI canónico en `openapi/citasalud-v1.yaml` (raíz del repo), según
`plan.md`.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Habilitar en el repositorio la infraestructura de build exigida
por la constitución (Principios IV, VI, VII), aún no configurada.

- [X] T001 Crear la estructura de paquetes `domain/{model,exception}`,
  `application/{port,usecase}`, `infrastructure/persistence`,
  `presentation/dto` bajo `src/main/java/org/ups/citasalud/`, y
  `unit/`, `integration/`, `functional/` bajo
  `src/test/java/org/ups/citasalud/`, según `plan.md` (Project Structure).
- [X] T002 Crear `openapi/citasalud-v1.yaml` en la raíz del repo a partir de
  `specs/001-online-appointment-booking/contracts/appointment-booking.openapi.yaml`
  (contrato canónico del proyecto, Principio IV).
- [X] T003 Añadir y configurar el plugin Gradle `org.openapi.generator` en
  `build.gradle`, apuntando a `openapi/citasalud-v1.yaml`, generador
  `spring`, `interfaceOnly=true`, para generar interfaces de controlador y
  DTOs (Principio IV; ver `research.md` §2).
- [X] T004 [P] Añadir y configurar el plugin `jacoco` en `build.gradle` con
  `jacocoTestCoverageVerification` (>80% línea/rama por clase y global)
  enlazado a la tarea `check` (Principio VI; ver `research.md` §4).
- [X] T005 [P] Añadir y configurar el plugin Gradle `checkstyle` en
  `build.gradle` con un ruleset basado en `google_checks.xml`, enlazado a la
  tarea `check` (Principio VII; ver `research.md` §4).

**Checkpoint**: `./gradlew.bat build` ejecuta generación OpenAPI, Checkstyle
y JaCoCo (aunque aún sin código de negocio ni cobertura suficiente).

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Modelo de dominio, puertos y persistencia compartidos por toda
la funcionalidad de esta feature (única historia de usuario).

**⚠️ CRITICAL**: Ninguna tarea de la Fase 3 puede completarse sin esta fase.

- [X] T006 [P] Crear el modelo de dominio `FranjaHoraria` (campos y estados
  `DISPONIBLE`/`RESERVADA` según `data-model.md`) en
  `src/main/java/org/ups/citasalud/domain/model/FranjaHoraria.java`, usando
  Lombok (`@Getter`/`@RequiredArgsConstructor` u equivalente) para
  boilerplate y un constructor/factory explícito para la transición de
  estado, sin setters públicos que permitan un estado inválido (Principio
  V).
- [X] T007 [P] Crear el modelo de dominio `Cita` (según `data-model.md`) en
  `src/main/java/org/ups/citasalud/domain/model/Cita.java`, usando Lombok
  (`@Getter`/`@RequiredArgsConstructor` u equivalente) para boilerplate y
  un constructor/factory explícito que garantice sus invariantes, sin
  setters públicos que permitan un estado inválido (Principio V).
- [X] T008 [P] Crear `FranjaNoDisponibleException` en
  `src/main/java/org/ups/citasalud/domain/exception/FranjaNoDisponibleException.java`.
- [X] T009 Definir el puerto `FranjaHorariaRepository` (interfaz de
  Application) con `listarDisponibles(...)` y
  `reservar(franjaHorariaId, pacienteId)` (transición atómica
  `DISPONIBLE → RESERVADA`, ver `research.md` §1) en
  `src/main/java/org/ups/citasalud/application/port/FranjaHorariaRepository.java`
  (depende de T006).
- [X] T010 Definir el puerto `CitaRepository` (interfaz de Application) con
  `guardar(Cita)` en
  `src/main/java/org/ups/citasalud/application/port/CitaRepository.java`
  (depende de T007).
- [X] T011 Crear `FranjaHorariaJpaEntity` y `CitaJpaEntity` (con `@Version`
  para bloqueo optimista en `FranjaHorariaJpaEntity`, ver `research.md` §1)
  en `src/main/java/org/ups/citasalud/infrastructure/persistence/` (depende
  de T006, T007).
- [X] T012 Implementar los adaptadores de los puertos T009/T010 —
  `FranjaHorariaJpaRepositoryAdapter` y `CitaJpaRepositoryAdapter` — con la
  actualización condicional atómica (`UPDATE ... WHERE estado = 'DISPONIBLE'`
  o equivalente Spring Data) en
  `src/main/java/org/ups/citasalud/infrastructure/persistence/` (depende de
  T009, T010, T011).
- [X] T013 Crear `src/main/resources/schema.sql` con el DDL explícito de las
  tablas `FRANJA_HORARIA` y `CITA` (incluida la columna de versión para
  bloqueo optimista y las restricciones de unicidad/estado de
  `research.md` §1), y configurar
  `spring.jpa.hibernate.ddl-auto=none` + `spring.sql.init.mode=always` en
  `src/main/resources/application.properties` (ver `research.md` §6;
  depende de T011).
- [X] T014 [P] Crear `src/main/resources/data.sql` con varias
  `FranjaHoraria` precargadas en estado `DISPONIBLE` para validar
  `quickstart.md` (ver `research.md` §6; depende de T013).

- [X] T026 [P] Extender el puerto `CitaRepository` (Application) con
  `existeSuperposicion(pacienteId, fechaHoraInicio, fechaHoraFin): boolean`
  (FR-011) y `buscarPorPacienteYFranja(pacienteId, franjaHorariaId):
  Optional<Cita>` (FR-012; reconciliación tras pérdida de conexión, ver
  spec.md Edge Cases) en
  `src/main/java/org/ups/citasalud/application/port/CitaRepository.java`
  (depende de T007, T010).
- [X] T027 [P] Crear `CitaSuperpuestaException` en
  `src/main/java/org/ups/citasalud/domain/exception/CitaSuperpuestaException.java`
  (FR-011).

**Checkpoint**: Modelo, puertos y persistencia (con esquema y datos
precargados) listos; la Fase 3 puede comenzar.

---

## Phase 3: User Story 1 - Reserva de cita en línea (autoservicio) (Priority: P1) 🎯 MVP

**Goal**: Un paciente puede consultar franjas disponibles y confirmar una
reserva; si la franja ya fue tomada, recibe "horario no disponible" y puede
elegir otra (spec.md, Acceptance Scenarios 1 y 2).

**Independent Test**: Ejecutar los 3 escenarios de `quickstart.md` contra el
servicio corriendo (`GET /franjas-horarias`, `POST /citas` exitoso, `POST
/citas` en conflicto, y las dos solicitudes concurrentes).

### Tests for User Story 1 (obligatorios por Principio II)

> Escribir estos tests PRIMERO; deben fallar antes de la implementación.

- [X] T015 [P] [US1] Unit test de `ReservarCitaUseCase` en
  `src/test/java/org/ups/citasalud/unit/application/ReservarCitaUseCaseTest.java`:
  Given franja `DISPONIBLE` / When se reserva / Then se confirma la `Cita`
  y el puerto marca la franja `RESERVADA`; Given franja ya `RESERVADA` /
  When se reserva / Then se lanza `FranjaNoDisponibleException` (puerto
  simulado con test double); y Given el paciente ya tiene una `Cita`
  `CONFIRMADA` que se superpone en horario con la franja solicitada
  (`existeSuperposicion` = true) / When se reserva / Then se lanza
  `CitaSuperpuestaException` y no se reserva la franja (FR-011).
  Corresponde a Acceptance Scenarios 1 y 2 del spec y a FR-011.
- [X] T016 [P] [US1] Integration test de concurrencia en
  `src/test/java/org/ups/citasalud/integration/FranjaHorariaJpaRepositoryAdapterIT.java`
  (contra H2 real, `@DataJpaTest` o equivalente, arrancando sobre el
  `schema.sql`/`data.sql` de T013/T014): Given una única franja
  `DISPONIBLE` / When dos solicitudes de reserva llegan prácticamente
  simultáneas / Then exactamente una tiene éxito y la otra falla con
  "no disponible" (Escenario 3 de `quickstart.md`, SC-003).
- [X] T017 [P] [US1] Functional test de `CitaController` con MockMvc en
  `src/test/java/org/ups/citasalud/functional/CitaControllerTest.java`:
  Given/When/Then para `GET /franjas-horarias` (200 con franjas disponibles),
  `POST /citas` exitoso (201, franja ya no listada como disponible después),
  `POST /citas` en conflicto por franja no disponible (409,
  `HORARIO_NO_DISPONIBLE` — cubre tanto franja tomada como franja
  inexistente, ver spec.md Edge Cases), `POST /citas` en conflicto por
  superposición de horario del paciente (409,
  `CONFLICTO_HORARIO_PACIENTE`, FR-011), `GET /citas` con
  `pacienteId`/`franjaHorariaId` (200 si la cita quedó registrada, 404 si
  no; FR-012), y el flujo de reintento tras 409 — Given una franja ya `RESERVADA`
  / When el paciente recibe `HORARIO_NO_DISPONIBLE` y repite `POST /citas`
  sobre otra franja `DISPONIBLE` en la misma llamada de test (sin reiniciar
  sesión ni estado) / Then obtiene 201 (FR-008). Corresponde a Escenarios 1
  y 2 de `quickstart.md`, a FR-008 y a FR-011.

### Implementation for User Story 1

- [X] T018 [US1] Implementar `ReservarCitaUseCase` en
  `src/main/java/org/ups/citasalud/application/usecase/ReservarCitaUseCase.java`
  usando los puertos `FranjaHorariaRepository` y `CitaRepository`; antes de
  reservar, invocar `CitaRepository.existeSuperposicion(...)` y lanzar
  `CitaSuperpuestaException` si hay conflicto de horario del paciente
  (FR-011) (depende de T009, T010, T026, T027; debe hacer pasar T015).
- [X] T019 [US1] Implementar `ListarFranjasDisponiblesUseCase` en
  `src/main/java/org/ups/citasalud/application/usecase/ListarFranjasDisponiblesUseCase.java`
  usando `FranjaHorariaRepository.listarDisponibles(...)` (depende de T009).
- [X] T020 [US1] Implementar `CitaController` (implementa la interfaz
  generada por OpenAPI Generator a partir de T003) en
  `src/main/java/org/ups/citasalud/presentation/CitaController.java`,
  exponiendo `GET /franjas-horarias`, `POST /citas` y `GET /citas` (FR-012
  para este último; depende de T018, T019, T028).
- [X] T021 [US1] Implementar los mappers DTO-generado ↔ modelo de dominio en
  `src/main/java/org/ups/citasalud/presentation/dto/` (depende de T020).
- [X] T028 [US1] Implementar `existeSuperposicion` y
  `buscarPorPacienteYFranja` (T026) en `CitaJpaRepositoryAdapter`, en
  `src/main/java/org/ups/citasalud/infrastructure/persistence/` (depende de
  T011, T012, T026).
- [X] T022 [US1] Implementar `GlobalExceptionHandler` (`@ControllerAdvice`)
  que traduce `FranjaNoDisponibleException` (franja tomada por otro
  paciente *o* franja inexistente/eliminada — ambos casos se tratan igual,
  ver spec.md Edge Cases) a `409` con cuerpo
  `{codigo: HORARIO_NO_DISPONIBLE, mensaje: "horario no disponible"}`, y
  `CitaSuperpuestaException` a `409` con cuerpo
  `{codigo: CONFLICTO_HORARIO_PACIENTE, mensaje: "..."}` (FR-011), según
  `contracts/appointment-booking.openapi.yaml`, en
  `src/main/java/org/ups/citasalud/presentation/GlobalExceptionHandler.java`
  (depende de T008, T020, T027; debe hacer pasar T017).

**Checkpoint**: User Story 1 (única historia del spec) completamente
funcional y probable de forma independiente vía `quickstart.md`. Esto
constituye el MVP completo de esta feature.

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Cerrar los gates de calidad de la constitución y validar el
feature de punta a punta.

- [X] T023 [P] Ejecutar manualmente los 3 escenarios de `quickstart.md`
  contra el servicio levantado con `./gradlew.bat bootRun` y confirmar los
  resultados esperados; incluye verificar de forma manual SC-001 (reserva
  completa en <2 min), SC-002 (disponible fuera de horario de atención) y
  SC-005 (elegir franja alternativa tras "horario no disponible", FR-008) —
  estos tres criterios son de experiencia de usuario y no tienen frontend
  en este repositorio, por lo que se validan de forma manual/narrativa vía
  `quickstart.md` en vez de un test automatizado.
- [X] T024 [P] Ejecutar `./gradlew.bat check` y confirmar que
  `jacocoTestCoverageVerification` pasa (>80% por clase y global, Principio
  VI) y que Checkstyle no reporta violaciones (Principio VII).
- [X] T025 [P] Añadir logging estructurado (sin datos sensibles) en
  `ReservarCitaUseCase` (confirmación y conflicto) y en
  `GlobalExceptionHandler` (Principio VII, producción-lista); añadir
  Javadoc a las interfaces de Application (`FranjaHorariaRepository`,
  `CitaRepository`, casos de uso) y al controlador de Presentation
  (`CitaController`) donde el intent no sea obvio (Principio VII).
- [X] T029 [P] Validar el objetivo de rendimiento p95 < 300 ms de
  `POST /citas` bajo carga concurrente moderada (decenas de solicitudes
  simultáneas por franja, ver plan.md Performance Goals) con una prueba de
  carga ligera (p. ej. script de solicitudes concurrentes reutilizando el
  patrón de T016); documentar el resultado en `quickstart.md` (SC-004).

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sin dependencias — puede iniciar de inmediato.
- **Foundational (Phase 2)**: Depende de Setup — bloquea toda la Fase 3.
- **User Story 1 (Phase 3)**: Depende de Foundational completo.
- **Polish (Phase 4)**: Depende de que la Fase 3 esté completa.

### Within User Story 1

- Tests (T015-T017) deben escribirse y fallar antes de T018-T022.
- Modelos/puertos (Foundational) antes de casos de uso (T018-T019).
- Casos de uso antes del controlador (T020).
- Controlador antes de mappers y manejo de errores (T021-T022).

### Parallel Opportunities

- T004 y T005 (Setup) en paralelo entre sí, tras T003.
- T006, T007, T008 (Foundational) en paralelo entre sí.
- T014 (data.sql) puede ir en paralelo respecto a tareas de otras ramas una
  vez completado T013 (schema.sql), del que depende directamente.
- T026, T027 (Foundational, FR-011 y reconciliación) en paralelo entre sí y
  respecto a T006-T014.
- T015, T016, T017 (tests de US1) en paralelo entre sí, antes de la
  implementación.
- T023, T024, T025, T029 (Polish) en paralelo entre sí.

---

## Parallel Example: User Story 1

```bash
# Lanzar juntos los tests de la User Story 1 (deben fallar primero):
Task: "Unit test ReservarCitaUseCase en src/test/java/org/ups/citasalud/unit/application/ReservarCitaUseCaseTest.java"
Task: "Integration test de concurrencia en src/test/java/org/ups/citasalud/integration/FranjaHorariaJpaRepositoryAdapterIT.java"
Task: "Functional test CitaController en src/test/java/org/ups/citasalud/functional/CitaControllerTest.java"
```

---

## Implementation Strategy

### MVP First (única historia de usuario)

1. Completar Fase 1: Setup (herramientas de build de la constitución).
2. Completar Fase 2: Foundational (modelo, puertos, persistencia, esquema y
   datos precargados).
3. Completar Fase 3: User Story 1 — tests primero, luego implementación.
4. **STOP y VALIDAR**: correr `quickstart.md` de punta a punta.
5. Completar Fase 4: Polish (cobertura, análisis estático, logging).

### Incremental Delivery

Al haber una única historia de usuario en este spec, la entrega incremental
de esta feature coincide con su MVP completo: Setup + Foundational + US1 +
Polish. Historias adicionales (si se agregan en el futuro al spec) se
incorporarían como nuevas fases posteriores a la Fase 3 actual, sin romper
lo aquí construido.

---

## Notes

- [P] = distintos archivos, sin dependencias entre sí.
- [US1] mapea cada tarea a la única historia de usuario del spec, para
  trazabilidad.
- T026-T028 y las actualizaciones de T015/T017/T018/T020/T022 fueron
  añadidos por `/speckit-analyze` para cerrar FR-011 (superposición de
  citas del mismo paciente) y la reconciliación de reserva tras pérdida de
  conexión (`GET /citas`), identificados como brechas de cobertura; esta
  última se formalizó como FR-012 en una pasada posterior de
  `/speckit-analyze`.
- Verificar que los tests (T015-T017) fallan antes de implementar
  (T018-T022).
- Hacer commit después de cada tarea o grupo lógico relacionado.
- Detenerse en el checkpoint de la Fase 3 para validar la historia de forma
  independiente antes de pasar a Polish.
