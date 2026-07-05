package org.ups.citasalud.presentation;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.ups.citasalud.application.port.CitaRepository;
import org.ups.citasalud.application.usecase.ListarFranjasDisponiblesUseCase;
import org.ups.citasalud.application.usecase.ReservarCitaUseCase;
import org.ups.citasalud.domain.model.Cita;
import org.ups.citasalud.presentation.dto.CitaDtoMapper;
import org.ups.citasalud.presentation.dto.FranjaHorariaDtoMapper;
import org.ups.citasalud.presentation.generated.api.CitasApi;
import org.ups.citasalud.presentation.generated.api.FranjasHorariasApi;
import org.ups.citasalud.presentation.generated.model.ReservaCitaRequest;

/**
 * Implementa la disponibilidad y reserva de citas (US-01) generadas a partir
 * de {@code openapi/citasalud-v1.yaml}.
 */
@RestController
public class CitaController implements FranjasHorariasApi, CitasApi {

    private final ListarFranjasDisponiblesUseCase listarFranjasDisponiblesUseCase;
    private final ReservarCitaUseCase reservarCitaUseCase;
    private final CitaRepository citaRepository;

    public CitaController(ListarFranjasDisponiblesUseCase listarFranjasDisponiblesUseCase,
            ReservarCitaUseCase reservarCitaUseCase, CitaRepository citaRepository) {
        this.listarFranjasDisponiblesUseCase = listarFranjasDisponiblesUseCase;
        this.reservarCitaUseCase = reservarCitaUseCase;
        this.citaRepository = citaRepository;
    }

    @Override
    public ResponseEntity<List<org.ups.citasalud.presentation.generated.model.FranjaHoraria>> listarFranjasDisponibles(
            String profesionalOServicioId) {
        List<org.ups.citasalud.presentation.generated.model.FranjaHoraria> franjas = listarFranjasDisponiblesUseCase
                .listar(Optional.ofNullable(profesionalOServicioId))
                .stream()
                .map(FranjaHorariaDtoMapper::aDto)
                .toList();
        return ResponseEntity.ok(franjas);
    }

    @Override
    public ResponseEntity<org.ups.citasalud.presentation.generated.model.Cita> reservarCita(
            ReservaCitaRequest reservaCitaRequest) {
        Cita cita = reservarCitaUseCase.reservar(
                reservaCitaRequest.getPacienteId(), reservaCitaRequest.getFranjaHorariaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CitaDtoMapper.aDto(cita));
    }

    /**
     * FR-012: permite al cliente reconciliar si una reserva quedo registrada
     * tras perder la conexion durante el POST /citas original (spec.md Edge
     * Cases), sin reintentar una reserva que ya se confirmo.
     */
    @Override
    public ResponseEntity<org.ups.citasalud.presentation.generated.model.Cita> buscarCitaPorPacienteYFranja(
            String pacienteId, String franjaHorariaId) {
        return citaRepository.buscarPorPacienteYFranja(pacienteId, franjaHorariaId)
                .map(cita -> ResponseEntity.ok(CitaDtoMapper.aDto(cita)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
