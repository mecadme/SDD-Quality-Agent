package org.ups.citasalud.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.ups.citasalud.domain.exception.FranjaNoDisponibleException;
import org.ups.citasalud.domain.model.FranjaHoraria;
import org.ups.citasalud.infrastructure.persistence.FranjaHorariaJpaRepositoryAdapter;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(FranjaHorariaJpaRepositoryAdapter.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class FranjaHorariaJpaRepositoryAdapterIT {

    private static final String FRANJA_DISPONIBLE_PRECARGADA = "FR-001";

    @Autowired
    private FranjaHorariaJpaRepositoryAdapter adapter;

    @Nested
    @DisplayName("Given una única FranjaHoraria DISPONIBLE")
    class DadaUnaFranjaDisponible {

        @Test
        @DisplayName("When dos reservas concurrentes llegan simultáneas Then exactamente una tiene éxito (SC-003)")
        void soloUnaReservaTieneExito() throws InterruptedException {
            int numeroDeHilos = 2;
            ExecutorService executor = Executors.newFixedThreadPool(numeroDeHilos);
            CountDownLatch listos = new CountDownLatch(numeroDeHilos);
            CountDownLatch salida = new CountDownLatch(1);
            List<Future<Boolean>> resultados = new ArrayList<>();

            for (int i = 0; i < numeroDeHilos; i++) {
                String pacienteId = "PAC-CONCURRENCIA-" + i;
                resultados.add(executor.submit(() -> {
                    listos.countDown();
                    salida.await();
                    try {
                        adapter.reservar(FRANJA_DISPONIBLE_PRECARGADA, pacienteId);
                        return true;
                    } catch (FranjaNoDisponibleException ex) {
                        return false;
                    }
                }));
            }

            listos.await();
            salida.countDown();

            long reservasExitosas = resultados.stream()
                    .map(this::obtenerResultado)
                    .filter(Boolean::booleanValue)
                    .count();
            executor.shutdown();

            assertThat(reservasExitosas).isEqualTo(1);
        }

        private boolean obtenerResultado(Future<Boolean> future) {
            try {
                return future.get();
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    @Nested
    @DisplayName("Given las franjas horarias precargadas (data.sql)")
    @Transactional
    class DadasFranjasPrecargadas {

        @Test
        @DisplayName("When listarDisponibles sin filtro Then incluye las franjas DISPONIBLES precargadas (FR-001)")
        void listarDisponiblesSinFiltro() {
            List<FranjaHoraria> disponibles = adapter.listarDisponibles(Optional.empty());

            assertThat(disponibles).extracting(FranjaHoraria::getId)
                    .contains("FR-002", "FR-003", "FR-004", "FR-005");
        }

        @Test
        @DisplayName("When listarDisponibles con filtro por profesional Then solo devuelve las de ese profesional")
        void listarDisponiblesConFiltro() {
            List<FranjaHoraria> disponibles = adapter.listarDisponibles(Optional.of("PROF-002"));

            assertThat(disponibles).extracting(FranjaHoraria::getId)
                    .containsExactlyInAnyOrder("FR-003", "FR-005");
        }

        @Test
        @DisplayName("When buscarPorId con un id existente Then devuelve la franja")
        void buscarPorIdExistente() {
            assertThat(adapter.buscarPorId("FR-002")).isPresent();
        }

        @Test
        @DisplayName("When buscarPorId con un id inexistente Then devuelve Optional vacio")
        void buscarPorIdInexistente() {
            assertThat(adapter.buscarPorId("FR-NO-EXISTE")).isEmpty();
        }
    }

    @Nested
    @DisplayName("Given una franja DISPONIBLE recién reservada por otro paciente")
    @Transactional
    class DadaUnaFranjaReservadaPorOtroPaciente {

        private static final String FRANJA_A_RESERVAR = "FR-004";

        @Test
        @DisplayName("When listarDisponibles Then ya no la incluye para el resto de pacientes (FR-009)")
        void franjaReservadaDejaDeListarseComoDisponible() throws Exception {
            assertThat(adapter.listarDisponibles(Optional.empty()))
                    .extracting(FranjaHoraria::getId)
                    .contains(FRANJA_A_RESERVAR);

            adapter.reservar(FRANJA_A_RESERVAR, "PAC-FR009");

            assertThat(adapter.listarDisponibles(Optional.empty()))
                    .extracting(FranjaHoraria::getId)
                    .doesNotContain(FRANJA_A_RESERVAR);
        }
    }
}
