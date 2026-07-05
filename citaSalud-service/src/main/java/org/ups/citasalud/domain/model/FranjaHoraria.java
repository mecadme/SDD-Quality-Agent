package org.ups.citasalud.domain.model;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * Bloque de tiempo ofrecido por la clinica para una atencion. La transicion
 * DISPONIBLE -&gt; RESERVADA es unidireccional en el alcance de esta feature.
 */
@Getter
public final class FranjaHoraria {

    public enum Estado {
        DISPONIBLE,
        RESERVADA
    }

    private final String id;
    private final LocalDateTime fechaHoraInicio;
    private final LocalDateTime fechaHoraFin;
    private final String profesionalOServicioId;
    private final Estado estado;

    public FranjaHoraria(String id, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin,
            String profesionalOServicioId, Estado estado) {
        if (!fechaHoraInicio.isBefore(fechaHoraFin)) {
            throw new IllegalArgumentException("fechaHoraInicio debe ser anterior a fechaHoraFin");
        }
        this.id = id;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.profesionalOServicioId = profesionalOServicioId;
        this.estado = estado;
    }

    public boolean estaDisponible() {
        return estado == Estado.DISPONIBLE;
    }
}
