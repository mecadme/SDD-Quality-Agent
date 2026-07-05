package org.ups.citasalud.presentation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.ups.citasalud.domain.exception.CitaSuperpuestaException;
import org.ups.citasalud.domain.exception.FranjaNoDisponibleException;
import org.ups.citasalud.presentation.generated.model.ErrorReserva;

/**
 * Traduce las excepciones de dominio a las respuestas 409 del contrato
 * (FR-007, FR-011).
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(FranjaNoDisponibleException.class)
    public ResponseEntity<ErrorReserva> manejarFranjaNoDisponible(FranjaNoDisponibleException ex) {
        log.info("Reserva rechazada por disponibilidad: {}", ex.getMessage());
        ErrorReserva error = new ErrorReserva(ErrorReserva.CodigoEnum.HORARIO_NO_DISPONIBLE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CitaSuperpuestaException.class)
    public ResponseEntity<ErrorReserva> manejarCitaSuperpuesta(CitaSuperpuestaException ex) {
        log.info("Reserva rechazada por superposicion de horario: {}", ex.getMessage());
        ErrorReserva error = new ErrorReserva(ErrorReserva.CodigoEnum.CONFLICTO_HORARIO_PACIENTE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
