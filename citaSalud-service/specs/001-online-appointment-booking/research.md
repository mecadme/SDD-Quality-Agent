# Research: Reserva de cita en línea (autoservicio)

No quedaron marcadores `NEEDS CLARIFICATION` en el Technical Context del
plan. Este documento resuelve las decisiones técnicas necesarias para pasar
a la fase de diseño.

## 1. Control de concurrencia para evitar dobles reservas

- **Decision**: Modelar `FranjaHorariaJpaEntity` con una columna de estado
  (`DISPONIBLE`/`RESERVADA`) protegida por bloqueo optimista de JPA
  (`@Version`), y además una restricción `UNIQUE` a nivel de base de datos
  sobre la franja reservada (p. ej. `unique(franja_id)` en la tabla de citas,
  o una transición de estado atómica `UPDATE ... WHERE estado = 'DISPONIBLE'`
  que solo tiene éxito si ninguna otra transacción ya la reservó). El caso de
  uso confirma la reserva únicamente si la actualización afecta una fila; si
  afecta cero filas, se lanza `FranjaNoDisponibleException`, que la capa de
  Presentation traduce al mensaje "horario no disponible".
- **Rationale**: Con múltiples instancias del servicio y solicitudes
  concurrentes sobre la misma franja, el bloqueo optimista + restricción a
  nivel de BD garantiza atomicidad sin depender de un lock distribuido
  externo. Es la solución más simple que satisface FR-005/SC-003.
- **Alternatives considered**:
  - *Bloqueo pesimista (`SELECT ... FOR UPDATE`)*: introduce contención
    innecesaria para el volumen esperado (decenas de solicitudes
    concurrentes, no miles); se descarta por complejidad/latencia
    adicional sin beneficio claro a esta escala.
  - *Lock distribuido (p. ej. Redis)*: añade una dependencia de
    infraestructura no justificada por el alcance actual (YAGNI, Principio
    III); se reconsideraría solo si el servicio escalara a múltiples
    réplicas con alta contención por franja.

## 2. Generación de código desde el contrato OpenAPI

- **Decision**: Añadir el plugin Gradle `org.openapi.generator` apuntando a
  `openapi/citasalud-v1.yaml`, generador `spring`, con `interfaceOnly=true`
  y `useSpringBoot3=true` (o equivalente soportado por Spring Boot 4) para
  generar solo las interfaces de controlador y los modelos DTO; la lógica de
  negocio vive en `presentation/CitaController.java`, que implementa la
  interfaz generada.
- **Rationale**: Cumple el Principio IV (API-First) sin duplicar a mano las
  firmas de controlador ni los DTOs; el build falla si el contrato y el
  código generado divergen, porque la generación ocurre en cada build.
- **Alternatives considered**:
  - *Contrato escrito a mano y DTOs manuales*: descartado, viola
    explícitamente el Principio IV (NON-NEGOTIABLE).
  - *springdoc-openapi (contract-last, generado desde el código)*: invierte
    el flujo exigido (el contrato debe preceder al código), se descarta.

## 3. Estructura de tests Given-When-Then sin nuevo framework BDD

- **Decision**: Usar JUnit 5 con clases `@Nested` y `@DisplayName` que
  describan explícitamente Given/When/Then, apoyado por
  `spring-boot-starter-webmvc-test` (MockMvc) para tests funcionales y
  `spring-boot-starter-data-jpa-test` (H2) para tests de integración de
  repositorio.
- **Rationale**: Las dependencias ya están en `build.gradle`; añadir Cucumber
  u otro runner Gherkin sería una abstracción no requerida por el Principio
  III (YAGNI) para el alcance actual.
- **Alternatives considered**:
  - *Cucumber/Gherkin*: aporta `.feature` files legibles por no-técnicos,
    pero introduce una dependencia y un runner adicional sin que el spec lo
    requiera; se descarta por ahora.

## 4. Cobertura de pruebas (JaCoCo) y análisis estático

- **Decision**: Añadir el plugin `jacoco` (incluido en Gradle) con
  `jacocoTestCoverageVerification` exigiendo >80% de cobertura de línea/rama
  por clase y global, enlazado a la tarea `check`. Añadir el plugin Gradle
  `checkstyle` (con un ruleset basado en `google_checks.xml` adaptado) como
  análisis estático mínimo viable, también enlazado a `check`.
- **Rationale**: Cumple los Principios VI y VII de forma directa y con
  herramientas estándar del ecosistema Gradle/Java, sin introducir
  dependencias exóticas.
- **Alternatives considered**:
  - *SpotBugs/PMD en vez de Checkstyle*: opciones válidas equivalentes; se
    elige Checkstyle por ser la opción más liviana para arrancar el gate de
    Principio VII en un repo nuevo. Puede complementarse más adelante sin
    romper este contrato.

## 5. Almacenamiento

- **Decision**: Usar H2 (ya configurado como única dependencia de base de
  datos) tanto para desarrollo/test como para el entorno inicial de esta
  feature.
- **Rationale**: Es la única dependencia de persistencia presente en el
  repositorio hoy; introducir otro motor de BD sin un requisito explícito
  violaría YAGNI (Principio III). Si se requiere un motor productivo
  distinto, es una decisión de infraestructura separada y explícita, no
  parte de esta feature.
- **Alternatives considered**: PostgreSQL/MySQL productivo — fuera de
  alcance sin un requisito que lo motive; documentado como posible trabajo
  futuro, no bloqueante aquí.

## 6. Esquema de base de datos y datos precargados

- **Decision**: Definir el esquema H2 de forma explícita en
  `src/main/resources/schema.sql` (tablas `FRANJA_HORARIA` y `CITA`, con la
  columna de versión para bloqueo optimista y las restricciones de
  unicidad/estado de `research.md` §1) y precargar datos de ejemplo en
  `src/main/resources/data.sql` (varias franjas horarias en estado
  `DISPONIBLE`). En `application.properties` se configura
  `spring.jpa.hibernate.ddl-auto=none` y `spring.sql.init.mode=always` para
  que Spring Boot ejecute siempre `schema.sql` + `data.sql` al arrancar,
  en vez de dejar el esquema implícito vía autogeneración de Hibernate.
- **Rationale**: Un esquema explícito y versionado en el control de código
  hace reproducible el arranque de H2 para desarrollo, tests de integración
  y la validación de `quickstart.md` (siempre hay franjas `DISPONIBLE` para
  probar los 3 escenarios), y documenta las restricciones de concurrencia
  (§1) directamente en el DDL en vez de depender únicamente de anotaciones
  JPA generadas.
- **Alternatives considered**:
  - *Hibernate `ddl-auto=create`/`update`*: esquema implícito, no
    versionado ni auditable, y no garantiza que las restricciones de
    concurrencia (unicidad/estado) queden expresadas a nivel de BD; se
    descarta.
  - *Flyway/Liquibase*: apropiado para evolución de esquema en producción a
    lo largo del tiempo, pero añade una dependencia y un mecanismo de
    migraciones no justificado por el alcance actual (una sola feature,
    esquema aún no productivo) — YAGNI, Principio III. Se reconsideraría si
    el proyecto necesita versionar migraciones incrementales en producción.
