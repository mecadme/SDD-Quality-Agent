package org.ups.citasalud.unit.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ups.citasalud.application.port.FranjaHorariaRepository;
import org.ups.citasalud.application.usecase.ListarFranjasDisponiblesUseCase;
import org.ups.citasalud.domain.model.FranjaHoraria;

@ExtendWith(MockitoExtension.class)
class ListarFranjasDisponiblesUseCaseTest {

    @Mock
    private FranjaHorariaRepository franjaHorariaRepository;

    private ListarFranjasDisponiblesUseCase useCase;

    private FranjaHoraria franja(String id, String profesionalOServicioId) {
        return new FranjaHoraria(id, LocalDateTime.of(2026, 7, 6, 9, 0),
                LocalDateTime.of(2026, 7, 6, 9, 30), profesionalOServicioId, FranjaHoraria.Estado.DISPONIBLE);
    }

    @Nested
    @DisplayName("Given franjas DISPONIBLES sin filtro de profesional")
    class DadasFranjasSinFiltro {

        @Test
        @DisplayName("When se listan Then se delega al repositorio y se devuelve el resultado (FR-001)")
        void listaTodasLasDisponibles() {
            useCase = new ListarFranjasDisponiblesUseCase(franjaHorariaRepository);
            when(franjaHorariaRepository.listarDisponibles(Optional.empty()))
                    .thenReturn(List.of(franja("FR-001", "PROF-001"), franja("FR-002", "PROF-002")));

            List<FranjaHoraria> resultado = useCase.listar(Optional.empty());

            assertThat(resultado).extracting(FranjaHoraria::getId).containsExactly("FR-001", "FR-002");
        }
    }

    @Nested
    @DisplayName("Given un filtro por profesional/servicio")
    class DadoUnFiltroPorProfesional {

        @Test
        @DisplayName("When se listan Then se delega el filtro al repositorio")
        void listaSoloLasDelProfesionalFiltrado() {
            useCase = new ListarFranjasDisponiblesUseCase(franjaHorariaRepository);
            when(franjaHorariaRepository.listarDisponibles(Optional.of("PROF-001")))
                    .thenReturn(List.of(franja("FR-001", "PROF-001")));

            List<FranjaHoraria> resultado = useCase.listar(Optional.of("PROF-001"));

            assertThat(resultado).extracting(FranjaHoraria::getId).containsExactly("FR-001");
        }
    }
}
