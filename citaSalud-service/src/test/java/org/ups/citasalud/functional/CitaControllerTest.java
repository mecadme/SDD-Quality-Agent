package org.ups.citasalud.functional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.ups.citasalud.application.port.CitaRepository;
import org.ups.citasalud.application.usecase.ListarFranjasDisponiblesUseCase;
import org.ups.citasalud.application.usecase.ReservarCitaUseCase;
import org.ups.citasalud.domain.exception.CitaSuperpuestaException;
import org.ups.citasalud.domain.exception.FranjaNoDisponibleException;
import org.ups.citasalud.domain.model.Cita;
import org.ups.citasalud.domain.model.FranjaHoraria;
import org.ups.citasalud.presentation.CitaController;

@WebMvcTest(controllers = CitaController.class)
class CitaControllerTest {

    private static final String FRANJA_ID = "FR-001";
    private static final String OTRA_FRANJA_ID = "FR-002";
    private static final String FRANJA_INEXISTENTE_ID = "FR-999";
    private static final String PACIENTE_ID = "PAC-001";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListarFranjasDisponiblesUseCase listarFranjasDisponiblesUseCase;

    @MockitoBean
    private ReservarCitaUseCase reservarCitaUseCase;

    @MockitoBean
    private CitaRepository citaRepository;

    private FranjaHoraria franja(String id) {
        return new FranjaHoraria(id, LocalDateTime.of(2026, 7, 6, 9, 0),
                LocalDateTime.of(2026, 7, 6, 9, 30), "PROF-001", FranjaHoraria.Estado.DISPONIBLE);
    }

    private Cita citaConfirmada(String franjaId) {
        return Cita.confirmar("CITA-001", PACIENTE_ID, franjaId, LocalDateTime.of(2026, 7, 4, 8, 0));
    }

    private String cuerpoReserva(String franjaId) {
        return "{\"franjaHorariaId\":\"" + franjaId + "\",\"pacienteId\":\"" + PACIENTE_ID + "\"}";
    }

    @Nested
    @DisplayName("Given franjas horarias DISPONIBLES")
    class DadasFranjasDisponibles {

        @Test
        @DisplayName("When GET /franjas-horarias Then responde 200 con la lista de franjas")
        void listaFranjasDisponibles() throws Exception {
            when(listarFranjasDisponiblesUseCase.listar(Optional.empty())).thenReturn(List.of(franja(FRANJA_ID)));

            mockMvc.perform(get("/franjas-horarias"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(FRANJA_ID))
                    .andExpect(jsonPath("$[0].estado").value("DISPONIBLE"));
        }
    }

    @Nested
    @DisplayName("Given una franja DISPONIBLE")
    class DadaUnaReservaExitosa {

        @Test
        @DisplayName("When POST /citas Then responde 201 y la franja deja de listarse como disponible")
        void reservaExitosaYFranjaYaNoDisponible() throws Exception {
            when(reservarCitaUseCase.reservar(PACIENTE_ID, FRANJA_ID)).thenReturn(citaConfirmada(FRANJA_ID));

            mockMvc.perform(post("/citas").contentType(MediaType.APPLICATION_JSON).content(cuerpoReserva(FRANJA_ID)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.estado").value("CONFIRMADA"))
                    .andExpect(jsonPath("$.pacienteId").value(PACIENTE_ID));

            when(listarFranjasDisponiblesUseCase.listar(Optional.empty())).thenReturn(List.of());

            mockMvc.perform(get("/franjas-horarias"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("Given una franja que ya no está DISPONIBLE")
    class DadoConflictoDeDisponibilidad {

        @Test
        @DisplayName("When POST /citas Then responde 409 HORARIO_NO_DISPONIBLE")
        void conflictoPorFranjaNoDisponible() throws Exception {
            when(reservarCitaUseCase.reservar(anyString(), eq(FRANJA_ID))).thenThrow(new FranjaNoDisponibleException());

            mockMvc.perform(post("/citas").contentType(MediaType.APPLICATION_JSON).content(cuerpoReserva(FRANJA_ID)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.codigo").value("HORARIO_NO_DISPONIBLE"))
                    .andExpect(jsonPath("$.mensaje").value("horario no disponible"));
        }
    }

    @Nested
    @DisplayName("Given una franja horaria inexistente")
    class DadoConflictoPorFranjaInexistente {

        @Test
        @DisplayName("When POST /citas Then responde 409 HORARIO_NO_DISPONIBLE")
        void conflictoPorFranjaInexistente() throws Exception {
            when(reservarCitaUseCase.reservar(anyString(), eq(FRANJA_INEXISTENTE_ID)))
                    .thenThrow(new FranjaNoDisponibleException());

            mockMvc.perform(post("/citas").contentType(MediaType.APPLICATION_JSON)
                            .content(cuerpoReserva(FRANJA_INEXISTENTE_ID)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.codigo").value("HORARIO_NO_DISPONIBLE"))
                    .andExpect(jsonPath("$.mensaje").value("horario no disponible"));
        }
    }

    @Nested
    @DisplayName("Given el paciente ya tiene una cita confirmada que se superpone")
    class DadoConflictoDeSuperposicion {

        @Test
        @DisplayName("When POST /citas Then responde 409 CONFLICTO_HORARIO_PACIENTE")
        void conflictoPorSuperposicionDeHorario() throws Exception {
            when(reservarCitaUseCase.reservar(anyString(), eq(FRANJA_ID))).thenThrow(new CitaSuperpuestaException());

            mockMvc.perform(post("/citas").contentType(MediaType.APPLICATION_JSON).content(cuerpoReserva(FRANJA_ID)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.codigo").value("CONFLICTO_HORARIO_PACIENTE"));
        }
    }

    @Nested
    @DisplayName("Given una reserva que puede o no haber quedado registrada (FR-012)")
    class DadaConsultaDeReconciliacion {

        @Test
        @DisplayName("When GET /citas y la cita existe Then responde 200")
        void citaEncontrada() throws Exception {
            when(citaRepository.buscarPorPacienteYFranja(PACIENTE_ID, FRANJA_ID))
                    .thenReturn(Optional.of(citaConfirmada(FRANJA_ID)));

            mockMvc.perform(get("/citas").param("pacienteId", PACIENTE_ID).param("franjaHorariaId", FRANJA_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.franjaHorariaId").value(FRANJA_ID));
        }

        @Test
        @DisplayName("When GET /citas y la cita no existe Then responde 404")
        void citaNoEncontrada() throws Exception {
            when(citaRepository.buscarPorPacienteYFranja(PACIENTE_ID, FRANJA_ID)).thenReturn(Optional.empty());

            mockMvc.perform(get("/citas").param("pacienteId", PACIENTE_ID).param("franjaHorariaId", FRANJA_ID))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Given el paciente recibió HORARIO_NO_DISPONIBLE (FR-008)")
    class DadoFlujoDeReintento {

        @Test
        @DisplayName("When repite POST /citas sobre otra franja DISPONIBLE Then obtiene 201")
        void reintentaConOtraFranjaYObtieneExito() throws Exception {
            when(reservarCitaUseCase.reservar(PACIENTE_ID, FRANJA_ID)).thenThrow(new FranjaNoDisponibleException());
            when(reservarCitaUseCase.reservar(PACIENTE_ID, OTRA_FRANJA_ID)).thenReturn(citaConfirmada(OTRA_FRANJA_ID));

            mockMvc.perform(post("/citas").contentType(MediaType.APPLICATION_JSON).content(cuerpoReserva(FRANJA_ID)))
                    .andExpect(status().isConflict());

            mockMvc.perform(post("/citas").contentType(MediaType.APPLICATION_JSON)
                            .content(cuerpoReserva(OTRA_FRANJA_ID)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.franjaHorariaId").value(OTRA_FRANJA_ID));
        }
    }
}
