package org.ups.citasalud.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ups.citasalud.domain.model.Cita;

@Entity
@Table(name = "CITA")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CitaJpaEntity {

    @Id
    private String id;

    private String pacienteId;

    private String franjaHorariaId;

    @Enumerated(EnumType.STRING)
    private Cita.Estado estado;

    private LocalDateTime fechaHoraCreacion;
}
