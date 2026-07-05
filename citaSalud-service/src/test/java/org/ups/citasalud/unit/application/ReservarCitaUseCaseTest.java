package org.ups.citasalud.unit.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ups.citasalud.application.port.CitaRepository;
import org.ups.citasalud.application.port.FranjaHorariaRepository;
import org.ups.citasalud.application.usecase.ReservarCitaUseCase;
import org.ups.citasalud.domain.exception.CitaSuperpuestaException;
import org.ups.citasalud.domain.exception.FranjaNoDisponibleException;
import org.ups.citasalud.domain.model.Cita;
import org.ups.citasalud.domain.model.FranjaHoraria;

@ExtendWith(MockitoExtension.class)
class ReservarCitaUseCaseTest {

    private static final String FRANJA_ID = "FR-001";
    private static final String PACIENTE_ID = "PAC-001";

    @Mock
    private FranjaHorariaRepository franjaHorariaRepository;

    @Mock
    private CitaRepository citaRepository;

    private ReservarCitaUseCase useCase;

    private FranjaHoraria franjaDisponible() {
        return new FranjaHoraria(FRANJA_ID, LocalDateTime.of(2026, 7, 6, 9, 0),
                LocalDateTime.of(2026, 7, 6, 9, 30), "PROF-001", FranjaHoraria.Estado.DISPONIBLE);
    }

    private FranjaHoraria franjaReservada() {
        return new FranjaHoraria(FRANJA_ID, LocalDateTime.of(2026, 7, 6, 9, 0),
                LocalDateTime.of(2026, 7, 6, 9, 30), "PROF-001", FranjaHoraria.Estado.RESERVADA);
    }

    @Nested
    @DisplayName("Given una franja DISPONIBLE")
    class DadaFranjaDisponible {

        @Test
        @DisplayName("When se reserva Then se confirma la Cita y se marca la franja RESERVADA")
        void confirmaLaReserva() {
            useCase = new ReservarCitaUseCase(franjaHorariaRepository, citaRepository);
            when(franjaHorariaRepository.buscarPorId(FRANJA_ID)).thenReturn(Optional.of(franjaDisponible()));
            when(citaRepository.existeSuperposicion(anyString(), any(), any())).thenReturn(false);
            when(citaRepository.guardar(any(Cita.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Cita cita = useCase.reservar(PACIENTE_ID, FRANJA_ID);

            assertThat(cita.getEstado()).isEqualTo(Cita.Estado.CONFIRMADA);
            assertThat(cita.getPacienteId()).isEqualTo(PACIENTE_ID);
            assertThat(cita.getFranjaHorariaId()).isEqualTo(FRANJA_ID);
            verify(franjaHorariaRepository).reservar(FRANJA_ID, PACIENTE_ID);
            verify(citaRepository).guardar(any(Cita.class));
        }
    }

    @Nested
    @DisplayName("Given una franja ya RESERVADA")
    class DadaFranjaYaReservada {

        @Test
        @DisplayName("When se reserva Then se lanza FranjaNoDisponibleException")
        void lanzaFranjaNoDisponible() {
            useCase = new ReservarCitaUseCase(franjaHorariaRepository, citaRepository);
            when(franjaHorariaRepository.buscarPorId(FRANJA_ID)).thenReturn(Optional.of(franjaReservada()));

            assertThatThrownBy(() -> useCase.reservar(PACIENTE_ID, FRANJA_ID))
                    .isInstanceOf(FranjaNoDisponibleException.class);

            verify(franjaHorariaRepository, never()).reservar(anyString(), anyString());
            verify(citaRepository, never()).guardar(any(Cita.class));
        }
    }

    @Nested
    @DisplayName("Given una franja horaria inexistente")
    class DadaFranjaInexistente {

        @Test
        @DisplayName("When se reserva Then se lanza FranjaNoDisponibleException")
        void lanzaFranjaNoDisponible() {
            useCase = new ReservarCitaUseCase(franjaHorariaRepository, citaRepository);
            when(franjaHorariaRepository.buscarPorId(FRANJA_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.reservar(PACIENTE_ID, FRANJA_ID))
                    .isInstanceOf(FranjaNoDisponibleException.class);

            verify(franjaHorariaRepository, never()).reservar(anyString(), anyString());
            verify(citaRepository, never()).guardar(any(Cita.class));
        }
    }

    @Nested
    @DisplayName("Given el paciente ya tiene una Cita CONFIRMADA que se superpone")
    class DadaSuperposicionDeHorario {

        @Test
        @DisplayName("When se reserva Then se lanza CitaSuperpuestaException y no se reserva la franja")
        void lanzaCitaSuperpuesta() {
            useCase = new ReservarCitaUseCase(franjaHorariaRepository, citaRepository);
            when(franjaHorariaRepository.buscarPorId(FRANJA_ID)).thenReturn(Optional.of(franjaDisponible()));
            when(citaRepository.existeSuperposicion(anyString(), any(), any())).thenReturn(true);

            assertThatThrownBy(() -> useCase.reservar(PACIENTE_ID, FRANJA_ID))
                    .isInstanceOf(CitaSuperpuestaException.class);

            verify(franjaHorariaRepository, never()).reservar(anyString(), anyString());
            verify(citaRepository, never()).guardar(any(Cita.class));
        }
    }
}
