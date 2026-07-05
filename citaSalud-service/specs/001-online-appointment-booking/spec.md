# Feature Specification: Reserva de cita en línea (autoservicio)

**Feature Branch**: `001-online-appointment-booking`

**Created**: 2026-07-04

**Status**: Draft

**Input**: User description: "### [US-01] Reserva de cita en línea (autoservicio)
Como **Paciente**, quiero reservar una cita en línea en cualquier franja
disponible, para no tener que llamar en mi hora de almuerzo ni hacer múltiples
intentos.

- Criterios de aceptación:
  - Dado que el paciente accede al sistema fuera del horario de atención de la
    clínica, cuando selecciona una franja libre y confirma, entonces la cita queda
    registrada, el slot queda bloqueado para otros y el paciente recibe
    confirmación inmediata.
  - Dado que el paciente intenta confirmar un slot que acaba de ser tomado por
    otro usuario en tiempo real, cuando confirma, entonces el sistema le muestra
    "horario no disponible" y le solicita elegir otra franja."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Reserva de cita en línea (autoservicio) (Priority: P1)

Como paciente, quiero poder reservar una cita disponible por mi cuenta, en
cualquier momento, sin depender de llamar por teléfono durante el horario de
atención de la clínica ni de reintentar varias veces para conseguir un cupo.

**Why this priority**: Es el flujo central de valor del sistema: sin la
capacidad de reservar en línea de forma confiable, no existe autoservicio.
Elimina la fricción de las llamadas telefónicas y reduce la carga operativa
del personal de la clínica.

**Independent Test**: Puede probarse de forma completa simulando a un
paciente que ingresa fuera del horario de atención, visualiza las franjas
disponibles, selecciona una y confirma; se valida que la cita quede
registrada, que la franja quede bloqueada para otros pacientes, y que se
reciba una confirmación inmediata. Este flujo por sí solo entrega valor
completo (un paciente puede agendar sin intervención humana).

**Acceptance Scenarios**:

1. **Given** el paciente accede al sistema fuera del horario de atención de la
   clínica, **When** selecciona una franja libre y confirma, **Then** la cita
   queda registrada, la franja queda bloqueada para otros pacientes y el
   paciente recibe confirmación inmediata.
2. **Given** el paciente intenta confirmar una franja que acaba de ser tomada
   por otro paciente en tiempo real, **When** confirma, **Then** el sistema le
   muestra el mensaje "horario no disponible" y le solicita elegir otra
   franja.

---

### Edge Cases

- ¿Qué sucede si el paciente pierde la conexión a internet justo después de
  confirmar, antes de recibir la confirmación en pantalla? El sistema debe
  permitirle verificar el estado real de la reserva (registrada o no) al
  reconectarse, sin generar una reserva duplicada.
- ¿Qué sucede si el paciente hace doble clic o envía la confirmación dos
  veces para la misma franja? El sistema debe procesar una única reserva y
  no debe generar citas duplicadas para el mismo paciente y franja.
- ¿Qué sucede si dos o más pacientes confirman la misma franja de forma
  prácticamente simultánea? Solo uno debe obtener la reserva; el resto debe
  recibir el aviso de "horario no disponible" sin quedar en un estado
  inconsistente.
- ¿Qué sucede si la franja seleccionada deja de existir (por ejemplo, fue
  eliminada o reprogramada por la clínica) entre que el paciente la visualiza
  y la confirma? Debe tratarse igual que una franja no disponible.
- ¿Qué sucede si el paciente ya tiene una cita registrada que se superpone
  con la nueva franja que intenta reservar? El sistema debe informarle el
  conflicto antes de completar la reserva.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir a los pacientes consultar las franjas
  horarias disponibles en cualquier momento, incluyendo fuera del horario de
  atención de la clínica (disponibilidad 24/7 de la funcionalidad de
  consulta y reserva).
- **FR-002**: El sistema MUST permitir a un paciente seleccionar una franja
  disponible y confirmar la reserva de una cita sobre esa franja.
- **FR-003**: El sistema MUST registrar la cita de forma persistente en
  cuanto la reserva es confirmada exitosamente.
- **FR-004**: El sistema MUST bloquear la franja reservada inmediatamente
  después de una confirmación exitosa, de modo que ningún otro paciente
  pueda seleccionarla o confirmarla posteriormente.
- **FR-005**: El sistema MUST garantizar que, ante intentos concurrentes de
  reserva sobre la misma franja, únicamente uno de ellos resulte en una
  cita registrada (sin dobles reservas de una misma franja).
- **FR-006**: El sistema MUST informar al paciente, de forma inmediata tras
  confirmar, que su cita fue registrada exitosamente (confirmación
  inmediata).
- **FR-007**: El sistema MUST detectar cuando un paciente intenta confirmar
  una franja que ya no está disponible (porque fue tomada por otro paciente
  u otro motivo) y mostrarle el mensaje "horario no disponible".
- **FR-008**: El sistema MUST permitir al paciente, tras recibir el aviso de
  no disponibilidad, elegir otra franja disponible sin tener que reiniciar
  todo el proceso de reserva desde cero.
- **FR-009**: El sistema MUST reflejar en tiempo real el estado de
  disponibilidad de las franjas, de modo que una franja recién tomada no
  continúe apareciendo como disponible para otros pacientes.
- **FR-010**: El sistema MUST asociar cada cita registrada con el paciente
  que la reservó y con la franja horaria correspondiente, de forma que la
  reserva quede trazable.
- **FR-011**: El sistema MUST rechazar la reserva de una franja que se
  superponga en fecha/hora con otra cita ya confirmada del mismo paciente,
  informando el conflicto antes de completar la reserva.
- **FR-012**: El sistema MUST permitir a un paciente consultar si una
  reserva quedó efectivamente registrada para una franja horaria dada
  (identificando la solicitud por `pacienteId` y `franjaHorariaId`), de
  modo que pueda verificar el estado real de su reserva tras perder la
  conexión antes de recibir la confirmación en pantalla, sin necesidad de
  reintentar la reserva ni generar una cita duplicada.

### Key Entities

- **Paciente**: persona que solicita y reserva una cita. Se identifica en el
  sistema mediante un perfil o cuenta existente (referenciado por
  `pacienteId`); la confirmación se recibe en la misma interacción
  (respuesta en pantalla), no mediante datos de contacto ni un canal
  adicional (ver Assumptions).
- **Franja Horaria (Slot)**: bloque de tiempo ofrecido por la clínica para
  una atención (fecha, hora de inicio y fin). Tiene un estado de
  disponibilidad (disponible / reservada) que determina si puede ser
  seleccionada y confirmada.
- **Cita (Reserva)**: registro que resulta de la confirmación exitosa de una
  franja por parte de un paciente. Vincula al paciente con la franja
  reservada y queda marcada como confirmada desde el momento de su
  creación.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Un paciente puede completar la reserva de una cita (desde la
  selección de una franja disponible hasta la confirmación) en menos de 2
  minutos.
- **SC-002**: La consulta y reserva de citas está disponible el 100% del
  tiempo, independientemente del horario de atención de la clínica (24/7).
- **SC-003**: Cuando dos o más pacientes intentan reservar la misma franja
  de forma simultánea, el sistema garantiza en el 100% de los casos que solo
  uno obtiene la reserva y el resto recibe el aviso de no disponibilidad, sin
  generar dobles reservas.
- **SC-004**: El paciente recibe la confirmación de su reserva en menos de
  300 ms (p95) desde que confirma, sin pasos adicionales.
- **SC-005**: Al menos el 95% de los pacientes que reciben el aviso de
  "horario no disponible" logran seleccionar y confirmar una franja
  alternativa sin abandonar el proceso de reserva.

**Nota de alcance**: SC-001, SC-002 y SC-005 son criterios de experiencia
de usuario que dependen de un cliente/frontend; este repositorio implementa
únicamente el servicio backend (ver plan.md, Project Type). Se validan de
forma manual/narrativa vía `quickstart.md` (que ejercita el flujo completo
de reintento de FR-008) en vez de mediante un test automatizado de UI.

## Assumptions

- El paciente ya cuenta con un perfil o cuenta identificable en el sistema
  (la autenticación/registro de pacientes se asume resuelta por un mecanismo
  estándar y está fuera del alcance de esta especificación).
- Las franjas horarias disponibles ya existen en el sistema (creadas o
  administradas por la clínica); esta especificación cubre la consulta y
  reserva de franjas existentes, no su creación o administración.
- La cancelación o reprogramación de una cita ya reservada está fuera del
  alcance de esta especificación; solo se cubre la reserva inicial.
- La confirmación inmediata al paciente se realiza dentro de la misma
  interacción (respuesta en pantalla); no se asume un canal adicional
  obligatorio (por ejemplo correo o SMS) como parte de esta funcionalidad.
- No se especifica un límite de anticipación para reservar (por ejemplo,
  reservar con meses de anticipación); se asume que cualquier franja futura
  marcada como disponible puede reservarse.
