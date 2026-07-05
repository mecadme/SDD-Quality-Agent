package org.ups.citasalud.domain.exception;

/**
 * Se lanza cuando una franja horaria ya no puede reservarse: fue tomada por
 * otro paciente, o dejo de existir (FR-007; ambos casos se tratan igual, ver
 * spec.md Edge Cases).
 */
public class FranjaNoDisponibleException extends RuntimeException {

    public FranjaNoDisponibleException() {
        super("horario no disponible");
    }
}
