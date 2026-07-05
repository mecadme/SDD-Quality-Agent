package org.ups.citasalud.application.usecase;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ups.citasalud.application.port.CitaRepository;
import org.ups.citasalud.application.port.FranjaHorariaRepository;
import org.ups.citasalud.domain.exception.CitaSuperpuestaException;
import org.ups.citasalud.domain.exception.FranjaNoDisponibleException;
import org.ups.citasalud.domain.model.Cita;
import org.ups.citasalud.domain.model.FranjaHoraria;

/**
 * Orquesta la reserva de una {@link FranjaHoraria} a nombre de un paciente
 * (US-01): valida superposicion de horario del paciente (FR-011) antes de
 * intentar la transicion atomica de la franja (FR-004, FR-005).
 */
@Slf4j
public class ReservarCitaUseCase {

    private final FranjaHorariaRepository franjaHorariaRepository;
    private final CitaRepository citaRepository;

    public ReservarCitaUseCase(FranjaHorariaRepository franjaHorariaRepository, CitaRepository citaRepository) {
        this.franjaHorariaRepository = franjaHorariaRepository;
        this.citaRepository = citaRepository;
    }

    public Cita reservar(String pacienteId, String franjaHorariaId) {
        FranjaHoraria franja = franjaHorariaRepository.buscarPorId(franjaHorariaId)
                .orElseThrow(FranjaNoDisponibleException::new);
        if (!franja.estaDisponible()) {
            log.info("Reserva rechazada: franjaHorariaId={} ya no estaba DISPONIBLE", franjaHorariaId);
            throw new FranjaNoDisponibleException();
        }

        boolean seSuperpone = citaRepository.existeSuperposicion(
                pacienteId, franja.getFechaHoraInicio(), franja.getFechaHoraFin());
        if (seSuperpone) {
            log.info("Reserva rechazada por superposicion de horario: pacienteId={} franjaHorariaId={}",
                    pacienteId, franjaHorariaId);
            throw new CitaSuperpuestaException();
        }

        franjaHorariaRepository.reservar(franjaHorariaId, pacienteId);

        Cita cita = Cita.confirmar(UUID.randomUUID().toString(), pacienteId, franjaHorariaId, LocalDateTime.now());
        Cita guardada = citaRepository.guardar(cita);
        log.info("Cita confirmada: citaId={} pacienteId={} franjaHorariaId={}",
                guardada.getId(), pacienteId, franjaHorariaId);
        return guardada;
    }
}
