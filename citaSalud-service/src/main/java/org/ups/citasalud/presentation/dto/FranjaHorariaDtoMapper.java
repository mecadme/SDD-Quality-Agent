package org.ups.citasalud.presentation.dto;

import java.time.ZoneId;
import org.ups.citasalud.domain.model.FranjaHoraria;

public final class FranjaHorariaDtoMapper {

    private FranjaHorariaDtoMapper() {
    }

    public static org.ups.citasalud.presentation.generated.model.FranjaHoraria aDto(FranjaHoraria franja) {
        org.ups.citasalud.presentation.generated.model.FranjaHoraria dto =
                new org.ups.citasalud.presentation.generated.model.FranjaHoraria();
        dto.setId(franja.getId());
        dto.setFechaHoraInicio(franja.getFechaHoraInicio().atZone(ZoneId.systemDefault()).toOffsetDateTime());
        dto.setFechaHoraFin(franja.getFechaHoraFin().atZone(ZoneId.systemDefault()).toOffsetDateTime());
        dto.setProfesionalOServicioId(franja.getProfesionalOServicioId());
        dto.setEstado(org.ups.citasalud.presentation.generated.model.FranjaHoraria.EstadoEnum
                .valueOf(franja.getEstado().name()));
        return dto;
    }
}
