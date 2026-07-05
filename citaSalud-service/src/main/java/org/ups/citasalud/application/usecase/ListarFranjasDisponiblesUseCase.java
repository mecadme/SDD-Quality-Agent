package org.ups.citasalud.application.usecase;

import java.util.List;
import java.util.Optional;
import org.ups.citasalud.application.port.FranjaHorariaRepository;
import org.ups.citasalud.domain.model.FranjaHoraria;

/**
 * Consulta las franjas horarias DISPONIBLES, opcionalmente filtradas por
 * profesional/servicio (FR-001, FR-009).
 */
public class ListarFranjasDisponiblesUseCase {

    private final FranjaHorariaRepository franjaHorariaRepository;

    public ListarFranjasDisponiblesUseCase(FranjaHorariaRepository franjaHorariaRepository) {
        this.franjaHorariaRepository = franjaHorariaRepository;
    }

    public List<FranjaHoraria> listar(Optional<String> profesionalOServicioId) {
        return franjaHorariaRepository.listarDisponibles(profesionalOServicioId);
    }
}
