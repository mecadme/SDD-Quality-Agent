package org.ups.citasalud.domain.model;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * Registro que resulta de la confirmacion exitosa de una {@link FranjaHoraria}
 * por parte de un paciente. Unica transicion soportada en el alcance de esta
 * feature: creacion directa en estado CONFIRMADA.
 */
@Getter
public final class Cita {

    public enum Estado {
        CONFIRMADA
    }

    private final String id;
    private final String pacienteId;
    private final String franjaHorariaId;
    private final Estado estado;
    private final LocalDateTime fechaHoraCreacion;

    public Cita(String id, String pacienteId, String franjaHorariaId, Estado estado,
            LocalDateTime fechaHoraCreacion) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.franjaHorariaId = franjaHorariaId;
        this.estado = estado;
        this.fechaHoraCreacion = fechaHoraCreacion;
    }

    public static Cita confirmar(String id, String pacienteId, String franjaHorariaId,
            LocalDateTime fechaHoraCreacion) {
        return new Cita(id, pacienteId, franjaHorariaId, Estado.CONFIRMADA, fechaHoraCreacion);
    }
}
