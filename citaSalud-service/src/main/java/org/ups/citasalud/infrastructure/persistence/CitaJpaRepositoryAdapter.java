package org.ups.citasalud.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.ups.citasalud.application.port.CitaRepository;
import org.ups.citasalud.domain.model.Cita;

@Repository
@RequiredArgsConstructor
public class CitaJpaRepositoryAdapter implements CitaRepository {

    private final CitaJpaRepository jpaRepository;

    @Override
    public Cita guardar(Cita cita) {
        CitaJpaEntity guardada = jpaRepository.save(toEntity(cita));
        return toDomain(guardada);
    }

    @Override
    public boolean existeSuperposicion(String pacienteId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
        return jpaRepository.existeSuperposicion(pacienteId, fechaHoraInicio, fechaHoraFin);
    }

    @Override
    public Optional<Cita> buscarPorPacienteYFranja(String pacienteId, String franjaHorariaId) {
        return jpaRepository.findByPacienteIdAndFranjaHorariaId(pacienteId, franjaHorariaId).map(this::toDomain);
    }

    private CitaJpaEntity toEntity(Cita cita) {
        return new CitaJpaEntity(cita.getId(), cita.getPacienteId(), cita.getFranjaHorariaId(),
                cita.getEstado(), cita.getFechaHoraCreacion());
    }

    private Cita toDomain(CitaJpaEntity entity) {
        return new Cita(entity.getId(), entity.getPacienteId(), entity.getFranjaHorariaId(),
                entity.getEstado(), entity.getFechaHoraCreacion());
    }
}
