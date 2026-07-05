package org.ups.citasalud.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface CitaJpaRepository extends JpaRepository<CitaJpaEntity, String> {

    Optional<CitaJpaEntity> findByPacienteIdAndFranjaHorariaId(String pacienteId, String franjaHorariaId);

    /**
     * FR-011: existe una Cita del paciente cuya franja se superpone con el
     * rango [fechaHoraInicio, fechaHoraFin).
     */
    @Query("SELECT COUNT(c) > 0 FROM CitaJpaEntity c JOIN FranjaHorariaJpaEntity f "
            + "ON f.id = c.franjaHorariaId "
            + "WHERE c.pacienteId = :pacienteId "
            + "AND f.fechaHoraInicio < :fechaHoraFin AND f.fechaHoraFin > :fechaHoraInicio")
    boolean existeSuperposicion(@Param("pacienteId") String pacienteId,
            @Param("fechaHoraInicio") LocalDateTime fechaHoraInicio,
            @Param("fechaHoraFin") LocalDateTime fechaHoraFin);
}
