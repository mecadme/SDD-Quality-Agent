# Implementation Plan: Reserva de cita en línea (autoservicio)

**Branch**: `001-online-appointment-booking` | **Date**: 2026-07-04 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-online-appointment-booking/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Los pacientes deben poder consultar franjas horarias disponibles y reservar
una cita en línea en cualquier momento (incluso fuera del horario de
atención), con la garantía de que dos pacientes no pueden reservar la misma
franja: la primera confirmación gana y bloquea el slot; cualquier intento
posterior sobre la misma franja recibe "horario no disponible" y puede elegir
otra. Se implementa como un servicio REST (Spring Boot) siguiendo Clean
Architecture: un caso de uso de Application orquesta las reglas de Domain
(Franja, Cita) sobre un puerto de repositorio implementado en Infrastructure
(JPA/H2), expuesto por un controlador de Presentation generado a partir de un
contrato OpenAPI.

## Technical Context

**Language/Version**: Java 25 (toolchain ya configurado en `build.gradle`)

**Primary Dependencies**: Spring Boot 4.1.0 (`spring-boot-starter-webmvc`,
`spring-boot-starter-data-jpa`, `spring-boot-h2console`), Lombok. A añadir en
esta feature: plugin Gradle de **OpenAPI Generator** (generación de
interfaces de controlador desde el contrato, Principio IV) y plugin Gradle de
**JaCoCo** + verificación de umbral (Principio VI); pendiente de un plugin de
análisis estático (Checkstyle o SpotBugs, Principio VII) — ver Research.

**Storage**: H2 (única dependencia de base de datos presente en el proyecto
hoy) vía Spring Data JPA. Se usa como almacén relacional para Franja Horaria
y Cita. El esquema (DDL) y los datos precargados de ejemplo se gestionan de
forma explícita mediante `src/main/resources/schema.sql` y
`src/main/resources/data.sql` (en vez de dejar el esquema implícito vía
Hibernate `ddl-auto`), para que la base H2 arranque siempre con las tablas y
franjas horarias de ejemplo listas (ver Research §6).

**Testing**: JUnit 5 (`junit-platform-launcher`) + `spring-boot-starter-webmvc-test`
(MockMvc, funcional) + `spring-boot-starter-data-jpa-test` (H2, integración).
Estructura Given-When-Then mediante `@Nested`/`@DisplayName`, sin añadir un
framework BDD adicional (YAGNI, Principio III).

**Target Platform**: Servidor Linux (servicio backend Spring Boot desplegado
como proceso independiente).

**Project Type**: web-service — proyecto único de backend (sin frontend en
este repositorio), estructurado en paquetes `domain`, `application`,
`infrastructure`, `presentation` (Principio I).

**Performance Goals**: Confirmación de reserva con p95 < 300 ms bajo carga
concurrente moderada (decenas de solicitudes simultáneas por franja); 0%
de dobles reservas bajo concurrencia (SC-003).

**Constraints**: No existe aún sistema de autenticación en el repositorio; el
alcance de esta feature (ver spec, sección Assumptions) asume que el
paciente ya está identificado — el contrato de la API recibe un
identificador de paciente sin implementar aquí el mecanismo de login/registro.
La prevención de doble reserva debe resolverse a nivel de base de datos
(no solo en memoria de la aplicación), dado que el servicio puede correr con
múltiples instancias.

**Scale/Scope**: Una clínica, catálogo pequeño/mediano de franjas horarias
por profesional/servicio; decenas de reservas concurrentes esperadas, no
miles. Alcance funcional: consulta de franjas disponibles + reserva +
manejo de conflicto de disponibilidad (US-01 del spec). Cancelación,
reprogramación, notificaciones por correo/SMS y administración de franjas
quedan fuera de alcance (ver Assumptions del spec).

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principio | Estado | Notas |
|-----------|--------|-------|
| I. Clean Architecture (NON-NEGOTIABLE) | PASS | Esta es la primera feature con código de dominio; se establecerán los paquetes `domain/application/infrastructure/presentation` desde cero, sin código previo que reorganizar. |
| II. BDD Testing Discipline (NON-NEGOTIABLE) | PASS | Se planifican los 3 niveles (unit, integración, funcional) en Given-When-Then; ver quickstart.md y tasks futuras. |
| III. SOLID / Clean Code | PASS | Un solo caso de uso (`ReservarCitaUseCase`) con puerto de repositorio; sin abstracciones especulativas adicionales. |
| IV. API-First con OpenAPI Generator (NON-NEGOTIABLE) | PASS (acción de setup requerida) | El repo aún no tiene `openapi/` ni el plugin `org.openapi.generator` en `build.gradle`. Esta feature los introduce (ver Research y contracts/). No es una excepción al principio, es su primera aplicación. |
| V. Lombok | PASS | Dependencia ya presente; se usará en entidades JPA y DTOs. |
| VI. Quality Gates vía JaCoCo (NON-NEGOTIABLE) | PASS (acción de setup requerida) | `build.gradle` no tiene JaCoCo configurado aún; se añade como parte de esta feature (umbral >80% por clase y global). |
| VII. Static Analysis & Production Readiness | PASS (acción de setup requerida) | No hay plugin de análisis estático configurado aún; se añade (Checkstyle, ver Research) como parte de esta feature. |

**Conclusión**: No hay violaciones que requieran excepción documentada en
Complexity Tracking. Las tres acciones de "setup requerida" (IV, VI, VII) son
trabajo de habilitación de la constitución en un repositorio nuevo, no
desviaciones de ella; se incorporan como tareas de esta feature en
`/speckit-tasks`.

**Re-chequeo post Fase 1**: revisados `data-model.md`, `contracts/` y
`quickstart.md` — no introducen entidades, dependencias ni acoplamientos
nuevos que cambien la evaluación anterior. Gate se mantiene en PASS.

**Actualización 2026-07-04**: se añadió a Technical Context, Project
Structure y `research.md` §6 la decisión de gestionar el esquema H2 y los
datos precargados vía `src/main/resources/schema.sql`/`data.sql` en vez de
`ddl-auto` implícito. Es una decisión de Infrastructure (mapeo JPA/DDL), no
afecta los límites de capas del Principio I ni introduce nuevas
dependencias externas; gate se mantiene en PASS.

## Project Structure

### Documentation (this feature)

```text
specs/001-online-appointment-booking/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
│   └── appointment-booking.openapi.yaml
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)

```text
openapi/
└── citasalud-v1.yaml            # Contrato OpenAPI canónico (Principio IV);
                                  # esta feature aporta las operaciones de
                                  # disponibilidad/reserva de citas.

src/main/resources/
├── application.properties       # spring.sql.init.mode=always; ddl-auto=none
├── schema.sql                   # DDL explícito: tablas FRANJA_HORARIA y CITA
└── data.sql                     # Datos precargados: franjas horarias DISPONIBLES de ejemplo

src/main/java/org/ups/citasalud/
├── domain/
│   ├── model/
│   │   ├── FranjaHoraria.java
│   │   └── Cita.java
│   └── exception/
│       └── FranjaNoDisponibleException.java
├── application/
│   ├── port/
│   │   └── FranjaHorariaRepository.java   # puerto (interfaz)
│   └── usecase/
│       └── ReservarCitaUseCase.java
├── infrastructure/
│   └── persistence/
│       ├── FranjaHorariaJpaEntity.java
│       ├── CitaJpaEntity.java
│       └── FranjaHorariaJpaRepository.java # implementa el puerto
└── presentation/
    ├── CitaController.java                 # implementa la interfaz generada
    └── dto/
        └── (mappers hacia/desde el modelo generado por OpenAPI Generator)

src/test/java/org/ups/citasalud/
├── unit/            # Domain + Application (test doubles para el puerto)
├── integration/     # Infrastructure contra H2 real (Spring Data JPA test)
└── functional/      # Presentation end-to-end vía MockMvc
```

**Structure Decision**: Proyecto único (backend Spring Boot), sin frontend en
este repositorio. Se adopta el layout de paquetes por capa
(`domain`/`application`/`infrastructure`/`presentation`) exigido por el
Principio I dentro de `src/main/java/org/ups/citasalud/`, y una carpeta de
tests por nivel (`unit`/`integration`/`functional`) que refleja el Principio
II. El contrato OpenAPI vive en `openapi/citasalud-v1.yaml` en la raíz del
repo, como exige la constitución (Technology & Architecture Standards); la
copia en `contracts/` de esta feature documenta específicamente las
operaciones que esta feature introduce. El esquema H2 y los datos de ejemplo
viven en `src/main/resources/schema.sql` y `src/main/resources/data.sql`
respectivamente (paquete `resources`, no en código Java), de modo que la
base de datos arranca siempre con el esquema y las franjas horarias
precargadas listas para `quickstart.md`.

## Complexity Tracking

> No aplica: el Constitution Check no reporta violaciones que requieran
> justificación. Las acciones de "setup requerida" (IV, VI, VII) son
> habilitación inicial del proyecto, no excepciones a un principio.
