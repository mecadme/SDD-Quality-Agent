package org.ups.citasalud.application.port;

import java.util.List;
import java.util.Optional;
import org.ups.citasalud.domain.exception.FranjaNoDisponibleException;
import org.ups.citasalud.domain.model.FranjaHoraria;

/**
 * Puerto de Application hacia la persistencia de {@link FranjaHoraria}.
 */
public interface FranjaHorariaRepository {

    /**
     * Franjas en estado DISPONIBLE (FR-001), filtradas por profesional/servicio
     * si se especifica (FR-009).
     */
    List<FranjaHoraria> listarDisponibles(Optional<String> profesionalOServicioId);

    Optional<FranjaHoraria> buscarPorId(String franjaHorariaId);

    /**
     * Transicion atomica DISPONIBLE -&gt; RESERVADA (FR-004, FR-005). Debe
     * lanzar {@link FranjaNoDisponibleException} si la franja no existe o ya
     * no estaba DISPONIBLE en el momento de la actualizacion.
     */
    FranjaHoraria reservar(String franjaHorariaId, String pacienteId);
}
