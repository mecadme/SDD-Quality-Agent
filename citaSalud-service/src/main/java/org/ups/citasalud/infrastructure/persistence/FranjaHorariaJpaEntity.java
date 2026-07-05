package org.ups.citasalud.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ups.citasalud.domain.model.FranjaHoraria;

@Entity
@Table(name = "FRANJA_HORARIA")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FranjaHorariaJpaEntity {

    @Id
    private String id;

    private LocalDateTime fechaHoraInicio;

    private LocalDateTime fechaHoraFin;

    @Column(name = "PROFESIONAL_O_SERVICIO_ID")
    private String profesionalOServicioId;

    @Enumerated(EnumType.STRING)
    private FranjaHoraria.Estado estado;

    @Version
    private Long version;
}
