package org.ups.citasalud.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * T029: valida el objetivo de rendimiento p95 &lt; 300 ms de POST /citas bajo
 * carga concurrente moderada (plan.md Performance Goals, SC-004). Reutiliza
 * el patron de solicitudes concurrentes de T016 (FranjaHorariaJpaRepositoryAdapterIT),
 * a nivel HTTP (MockMvc) en vez de a nivel de adaptador.
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class ReservaCitaPerformanceTest {

    private static final List<String> FRANJAS_PRECARGADAS = List.of("FR-001", "FR-002", "FR-003", "FR-004", "FR-005");
    private static final int SOLICITUDES_POR_FRANJA = 6;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("Given carga concurrente moderada sobre POST /citas (plan.md Performance Goals)")
    class DadaCargaConcurrenteModerada {

        @Test
        @DisplayName("When se disparan decenas de solicitudes simultaneas por franja Then el p95 es menor a 300 ms "
                + "(SC-004)")
        void p95MenorA300Ms() throws Exception {
            for (int i = 0; i < 20; i++) {
                mockMvc.perform(get("/franjas-horarias"));
                mockMvc.perform(post("/citas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"franjaHorariaId\":\"FR-NO-EXISTE\",\"pacienteId\":\"PAC-WARMUP\"}"));
            }

            List<Callable<Long>> tareas = new ArrayList<>();
            int contador = 0;
            for (String franjaId : FRANJAS_PRECARGADAS) {
                for (int i = 0; i < SOLICITUDES_POR_FRANJA; i++) {
                    String pacienteId = "PAC-CARGA-" + contador++;
                    tareas.add(() -> ejecutarReserva(pacienteId, franjaId));
                }
            }

            ExecutorService executor = Executors.newFixedThreadPool(tareas.size());
            List<Future<Long>> futuros = executor.invokeAll(tareas);
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);

            List<Long> latenciasMs = new ArrayList<>();
            for (Future<Long> futuro : futuros) {
                latenciasMs.add(futuro.get());
            }
            latenciasMs.sort(Long::compareTo);
            long p95 = latenciasMs.get((int) Math.ceil(latenciasMs.size() * 0.95) - 1);
            log.info("Carga concurrente POST /citas: {} solicitudes, p95={} ms", latenciasMs.size(), p95);

            assertThat(p95).isLessThan(300L);
        }

        private long ejecutarReserva(String pacienteId, String franjaId) throws Exception {
            String cuerpo = "{\"franjaHorariaId\":\"" + franjaId + "\",\"pacienteId\":\"" + pacienteId + "\"}";
            long inicio = System.nanoTime();
            mockMvc.perform(post("/citas").contentType(MediaType.APPLICATION_JSON).content(cuerpo));
            return (System.nanoTime() - inicio) / 1_000_000;
        }
    }
}
