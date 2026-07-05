package org.ups.citasalud.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ups.citasalud.application.port.CitaRepository;
import org.ups.citasalud.application.port.FranjaHorariaRepository;
import org.ups.citasalud.application.usecase.ListarFranjasDisponiblesUseCase;
import org.ups.citasalud.application.usecase.ReservarCitaUseCase;

/**
 * Cablea los casos de uso de Application (clases sin dependencias de
 * framework, Principio I) como beans de Spring.
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    public ReservarCitaUseCase reservarCitaUseCase(
            FranjaHorariaRepository franjaHorariaRepository, CitaRepository citaRepository) {
        return new ReservarCitaUseCase(franjaHorariaRepository, citaRepository);
    }

    @Bean
    public ListarFranjasDisponiblesUseCase listarFranjasDisponiblesUseCase(
            FranjaHorariaRepository franjaHorariaRepository) {
        return new ListarFranjasDisponiblesUseCase(franjaHorariaRepository);
    }
}
