package org.ups.citasalud.application.port;

import java.time.LocalDateTime;
import java.util.Optional;
import org.ups.citasalud.domain.model.Cita;

/**
 * Puerto de Application hacia la persistencia de {@link Cita}.
 */
public interface CitaRepository {

    /**
     * Persiste una {@link Cita} ya CONFIRMADA (FR-006).
     */
    Cita guardar(Cita cita);

    /**
     * FR-011: true si el paciente ya tiene una Cita CONFIRMADA cuya franja se
     * superpone con el rango [fechaHoraInicio, fechaHoraFin).
     */
    boolean existeSuperposicion(String pacienteId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin);

    /**
     * FR-012: reconciliacion tras perdida de conexion (ver spec.md Edge Cases).
     */
    Optional<Cita> buscarPorPacienteYFranja(String pacienteId, String franjaHorariaId);
}
