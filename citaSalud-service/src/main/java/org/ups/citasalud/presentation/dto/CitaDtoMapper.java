package org.ups.citasalud.presentation.dto;

import java.time.ZoneId;
import org.ups.citasalud.domain.model.Cita;

public final class CitaDtoMapper {

    private CitaDtoMapper() {
    }

    public static org.ups.citasalud.presentation.generated.model.Cita aDto(Cita cita) {
        org.ups.citasalud.presentation.generated.model.Cita dto =
                new org.ups.citasalud.presentation.generated.model.Cita();
        dto.setId(cita.getId());
        dto.setPacienteId(cita.getPacienteId());
        dto.setFranjaHorariaId(cita.getFranjaHorariaId());
        dto.setEstado(org.ups.citasalud.presentation.generated.model.Cita.EstadoEnum.valueOf(cita.getEstado().name()));
        dto.setFechaHoraCreacion(cita.getFechaHoraCreacion().atZone(ZoneId.systemDefault()).toOffsetDateTime());
        return dto;
    }
}
