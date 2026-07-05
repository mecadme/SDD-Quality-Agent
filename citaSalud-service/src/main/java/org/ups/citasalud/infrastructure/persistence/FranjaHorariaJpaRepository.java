package org.ups.citasalud.infrastructure.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.ups.citasalud.domain.model.FranjaHoraria;

interface FranjaHorariaJpaRepository extends JpaRepository<FranjaHorariaJpaEntity, String> {

    List<FranjaHorariaJpaEntity> findByEstado(FranjaHoraria.Estado estado);

    List<FranjaHorariaJpaEntity> findByEstadoAndProfesionalOServicioId(
            FranjaHoraria.Estado estado, String profesionalOServicioId);

    /**
     * Transicion atomica: solo actualiza si la franja seguia DISPONIBLE
     * (research.md #1). El numero de filas afectadas es la fuente de verdad
     * de si la reserva tuvo exito bajo concurrencia (FR-005).
     */
    @Modifying
    @Query("UPDATE FranjaHorariaJpaEntity f SET f.estado = :reservada "
            + "WHERE f.id = :id AND f.estado = :disponible")
    int reservarSiDisponible(@Param("id") String id, @Param("disponible") FranjaHoraria.Estado disponible,
            @Param("reservada") FranjaHoraria.Estado reservada);
}
