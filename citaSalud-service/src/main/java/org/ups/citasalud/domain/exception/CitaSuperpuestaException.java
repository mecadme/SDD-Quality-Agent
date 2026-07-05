package org.ups.citasalud.domain.exception;

/**
 * Se lanza cuando el paciente ya tiene una cita confirmada que se superpone
 * en fecha/hora con la franja solicitada (FR-011).
 */
public class CitaSuperpuestaException extends RuntimeException {

    public CitaSuperpuestaException() {
        super("el paciente ya tiene una cita confirmada que se superpone con la franja solicitada");
    }
}
