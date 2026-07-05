package org.ups.citasalud.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.ups.citasalud.domain.model.Cita;
import org.ups.citasalud.infrastructure.persistence.CitaJpaRepositoryAdapter;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CitaJpaRepositoryAdapter.class)
@Transactional
class CitaJpaRepositoryAdapterIT {

    @Autowired
    private CitaJpaRepositoryAdapter adapter;

    @Nested
    @DisplayName("Given una franja horaria precargada (FR-002)")
    class DadaUnaFranjaPrecargada {

        @Test
        @DisplayName("When se guarda una Cita Then queda persistida y es recuperable por paciente y franja (FR-012)")
        void guardarYBuscarPorPacienteYFranja() {
            Cita cita = Cita.confirmar("CITA-IT-001", "PAC-IT-001", "FR-002", LocalDateTime.now());

            adapter.guardar(cita);

            assertThat(adapter.buscarPorPacienteYFranja("PAC-IT-001", "FR-002"))
                    .isPresent()
                    .get()
                    .extracting(Cita::getId)
                    .isEqualTo("CITA-IT-001");
        }

        @Test
        @DisplayName("When se busca una Cita inexistente Then devuelve Optional vacio (FR-012)")
        void buscarPorPacienteYFranjaInexistente() {
            assertThat(adapter.buscarPorPacienteYFranja("PAC-SIN-CITA", "FR-002")).isEmpty();
        }
    }

    @Nested
    @DisplayName("Given el paciente ya tiene una Cita CONFIRMADA (FR-011)")
    class DadaUnaCitaConfirmadaPrevia {

        @Test
        @DisplayName("When se consulta superposicion con una franja que se solapa Then devuelve true")
        void existeSuperposicionConFranjaSolapada() {
            adapter.guardar(Cita.confirmar("CITA-IT-002", "PAC-IT-002", "FR-003", LocalDateTime.now()));

            boolean seSuperpone = adapter.existeSuperposicion("PAC-IT-002",
                    LocalDateTime.of(2026, 7, 6, 11, 15), LocalDateTime.of(2026, 7, 6, 11, 45));

            assertThat(seSuperpone).isTrue();
        }

        @Test
        @DisplayName("When se consulta superposicion con un horario distinto Then devuelve false")
        void existeSuperposicionSinSolape() {
            adapter.guardar(Cita.confirmar("CITA-IT-003", "PAC-IT-003", "FR-004", LocalDateTime.now()));

            boolean seSuperpone = adapter.existeSuperposicion("PAC-IT-003",
                    LocalDateTime.of(2026, 7, 7, 10, 0), LocalDateTime.of(2026, 7, 7, 10, 30));

            assertThat(seSuperpone).isFalse();
        }
    }
}
