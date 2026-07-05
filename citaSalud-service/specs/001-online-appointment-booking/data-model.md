# Data Model: Reserva de cita en línea (autoservicio)

Deriva de las Key Entities y Functional Requirements del [spec.md](./spec.md).
Estos son modelos de **Domain** (sin anotaciones de framework); su mapeo a
JPA es una preocupación de Infrastructure y no se detalla aquí.

## FranjaHoraria

Representa un bloque de tiempo ofrecido por la clínica que puede ser
reservado por un paciente.

| Campo | Tipo | Reglas / Notas |
|-------|------|-----------------|
| `id` | identificador único | asignado por el sistema |
| `fechaHoraInicio` | fecha y hora | debe ser anterior a `fechaHoraFin` |
| `fechaHoraFin` | fecha y hora | debe ser posterior a `fechaHoraInicio` |
| `profesionalOServicioId` | identificador | referencia al profesional/servicio al que pertenece la franja (fuera de alcance de esta feature su administración; se asume ya existente — ver spec Assumptions) |
| `estado` | enum: `DISPONIBLE`, `RESERVADA` | transición unidireccional `DISPONIBLE → RESERVADA` al confirmarse una cita (FR-004); no hay transición de vuelta en el alcance de esta feature (cancelación fuera de alcance) |

**Invariantes**:
- Una franja en estado `RESERVADA` no puede volver a ser seleccionada ni
  confirmada por otro paciente (FR-004, FR-005, FR-009).
- La transición a `RESERVADA` sólo ocurre si la franja estaba `DISPONIBLE`
  en el momento de la confirmación (ver research.md §1, control de
  concurrencia).

## Cita

Registro que resulta de la confirmación exitosa de una `FranjaHoraria` por
un `Paciente`.

| Campo | Tipo | Reglas / Notas |
|-------|------|-----------------|
| `id` | identificador único | asignado por el sistema; sirve como número de confirmación (FR-006) |
| `pacienteId` | identificador | paciente que reservó la cita (FR-010); se asume ya identificado (ver spec Assumptions) |
| `franjaHorariaId` | identificador | franja reservada (FR-010), relación 1:1 con `FranjaHoraria` |
| `estado` | enum: `CONFIRMADA` | única transición soportada en el alcance de esta feature; cancelación/reprogramación fuera de alcance |
| `fechaHoraCreacion` | fecha y hora | momento de la confirmación, usado para trazabilidad |

**Invariantes**:
- Una `Cita` sólo existe si su `FranjaHoraria` asociada pasó a `RESERVADA`
  en la misma operación (consistencia transaccional, FR-003/FR-004).
- No puede haber más de una `Cita` `CONFIRMADA` para la misma
  `franjaHorariaId` (FR-005).
- No puede haber dos `Cita` `CONFIRMADA` del mismo `pacienteId` cuyas
  `FranjaHoraria` asociadas se superpongan en el tiempo (FR-011).

## Paciente

No es una entidad gestionada por esta feature (ver spec Assumptions: la
identificación/autenticación del paciente se asume resuelta por un mecanismo
externo/estándar). Para esta feature, el `Paciente` se referencia únicamente
por su `pacienteId`, recibido como parte de la solicitud de reserva.

## Relaciones

```text
Paciente (externo) --reserva--> Cita --referencia 1:1--> FranjaHoraria
```

- Una `FranjaHoraria` tiene cero o una `Cita` asociada (según su `estado`).
- Una `Cita` referencia exactamente una `FranjaHoraria` y un `pacienteId`.
