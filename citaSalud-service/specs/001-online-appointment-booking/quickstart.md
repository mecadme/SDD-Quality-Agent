# Quickstart: Reserva de cita en línea (autoservicio)

Guía de validación end-to-end de US-01. No incluye código de implementación
(ver `data-model.md` y `contracts/appointment-booking.openapi.yaml` para el
detalle de modelo y API).

## Prerrequisitos

- JDK 25 instalado (o configurado vía toolchain de Gradle).
- Sin servicios externos: se usa H2 en memoria.
- Datos de prueba: el servicio arranca con el esquema y datos ya precargados
  desde `src/main/resources/schema.sql` y `src/main/resources/data.sql`
  (ver `research.md` §6), que incluyen al menos una `FranjaHoraria` en
  estado `DISPONIBLE`. No se requiere carga manual de datos.

## Levantar el servicio

```powershell
./gradlew.bat bootRun
```

## Escenario 1 — Reserva exitosa fuera de horario de atención (FR-001..FR-006, SC-001, SC-002)

1. **Given**: existe una `FranjaHoraria` con `estado = DISPONIBLE`
   (consultar `GET /franjas-horarias`).
2. **When**: se envía `POST /citas` con el `franjaHorariaId` de esa franja y
   un `pacienteId` válido.
3. **Then**:
   - La respuesta es `201 Created` con el cuerpo `Cita` (`estado =
     CONFIRMADA`), en menos de 2 minutos de interacción percibida por el
     usuario (SC-001).
   - Una consulta posterior a `GET /franjas-horarias` ya no incluye esa
     franja entre las disponibles (FR-004, FR-009).
   - Esto es válido en cualquier momento del día, sin depender del horario
     de atención de la clínica (SC-002).

## Escenario 2 — Conflicto de disponibilidad en tiempo real (FR-005, FR-007, FR-008, SC-003)

1. **Given**: una `FranjaHoraria` en estado `DISPONIBLE` es reservada
   exitosamente por el Paciente A (ver Escenario 1).
2. **When**: el Paciente B envía `POST /citas` con el mismo
   `franjaHorariaId`, después de que A ya la confirmó.
3. **Then**:
   - La respuesta es `409 Conflict` con `codigo = HORARIO_NO_DISPONIBLE` y
     `mensaje = "horario no disponible"` (FR-007).
   - No se crea una segunda `Cita` para esa franja (FR-005, SC-003).
   - El Paciente B puede repetir el flujo del Escenario 1 sobre otra franja
     `DISPONIBLE` sin reiniciar todo el proceso (FR-008).

## Escenario 3 — Concurrencia real (SC-003)

1. **Given**: una única `FranjaHoraria` `DISPONIBLE`.
2. **When**: se disparan dos solicitudes `POST /citas` prácticamente
   simultáneas (por ejemplo, dos hilos/clientes) sobre esa misma franja.
3. **Then**: exactamente una recibe `201 Created`; la otra recibe `409
   Conflict` con `HORARIO_NO_DISPONIBLE`. Nunca ambas reciben `201`.

## Validación de rendimiento (SC-004)

`ReservaCitaPerformanceTest` (T029) dispara 30 solicitudes `POST /citas`
simultáneas (6 por cada una de las 5 franjas precargadas por `data.sql`),
tras un calentamiento de JIT/pool de conexiones, y mide el p95 de latencia
end-to-end (MockMvc) contra el objetivo de `plan.md` (p95 < 300 ms bajo
carga concurrente moderada). Resultado observado en este entorno: **p95 ≈
126–141 ms** en ejecuciones repetidas, muy por debajo del umbral. La primera
ejecución sin calentamiento mostró p95 ≈ 623 ms, dominado por el
establecimiento de conexiones JDBC (pool de Hikari en 10 por defecto); se
amplió `spring.datasource.hikari.maximum-pool-size` a 30 en
`application.properties` para reflejar la concurrencia moderada esperada
("decenas de solicitudes", `research.md` §1) y se añadió un calentamiento
previo al test.

## Trazabilidad con los tests de la feature

| Escenario aquí | Nivel de test (Principio II) |
|-----------------|------------------------------|
| Escenario 1 | Funcional (MockMvc) + Unit (`ReservarCitaUseCase`) |
| Escenario 2 | Funcional (MockMvc) + Unit (`ReservarCitaUseCase`) |
| Escenario 3 | Integración (contra H2 real, con solicitudes concurrentes) |
