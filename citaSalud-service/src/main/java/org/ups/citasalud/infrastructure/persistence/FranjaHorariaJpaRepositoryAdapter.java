package org.ups.citasalud.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ups.citasalud.application.port.FranjaHorariaRepository;
import org.ups.citasalud.domain.exception.FranjaNoDisponibleException;
import org.ups.citasalud.domain.model.FranjaHoraria;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FranjaHorariaJpaRepositoryAdapter implements FranjaHorariaRepository {

    private final FranjaHorariaJpaRepository jpaRepository;

    @Override
    public List<FranjaHoraria> listarDisponibles(Optional<String> profesionalOServicioId) {
        List<FranjaHorariaJpaEntity> entidades = profesionalOServicioId
                .map(id -> jpaRepository.findByEstadoAndProfesionalOServicioId(FranjaHoraria.Estado.DISPONIBLE, id))
                .orElseGet(() -> jpaRepository.findByEstado(FranjaHoraria.Estado.DISPONIBLE));
        return entidades.stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<FranjaHoraria> buscarPorId(String franjaHorariaId) {
        return jpaRepository.findById(franjaHorariaId).map(this::toDomain);
    }

    @Override
    @Transactional
    public FranjaHoraria reservar(String franjaHorariaId, String pacienteId) {
        int actualizadas = jpaRepository.reservarSiDisponible(
                franjaHorariaId, FranjaHoraria.Estado.DISPONIBLE, FranjaHoraria.Estado.RESERVADA);
        if (actualizadas == 0) {
            log.info("Intento de reserva rechazado: franjaHorariaId={} ya no estaba DISPONIBLE", franjaHorariaId);
            throw new FranjaNoDisponibleException();
        }
        log.info("Franja reservada: franjaHorariaId={} pacienteId={}", franjaHorariaId, pacienteId);
        return jpaRepository.findById(franjaHorariaId)
                .map(this::toDomain)
                .orElseThrow(FranjaNoDisponibleException::new);
    }

    private FranjaHoraria toDomain(FranjaHorariaJpaEntity entity) {
        return new FranjaHoraria(entity.getId(), entity.getFechaHoraInicio(), entity.getFechaHoraFin(),
                entity.getProfesionalOServicioId(), entity.getEstado());
    }
}
