package org.ups.citasalud.presentation.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * FranjaHoraria
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-07-04T16:04:14.475931500-05:00[America/Guayaquil]", comments = "Generator version: 7.16.0")
public class FranjaHoraria {

  private String id;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime fechaHoraInicio;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime fechaHoraFin;

  private @Nullable String profesionalOServicioId;

  /**
   * Gets or Sets estado
   */
  public enum EstadoEnum {
    DISPONIBLE("DISPONIBLE"),
    
    RESERVADA("RESERVADA");

    private final String value;

    EstadoEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static EstadoEnum fromValue(String value) {
      for (EstadoEnum b : EstadoEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private EstadoEnum estado;

  public FranjaHoraria() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public FranjaHoraria(String id, OffsetDateTime fechaHoraInicio, OffsetDateTime fechaHoraFin, EstadoEnum estado) {
    this.id = id;
    this.fechaHoraInicio = fechaHoraInicio;
    this.fechaHoraFin = fechaHoraFin;
    this.estado = estado;
  }

  public FranjaHoraria id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull 
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public FranjaHoraria fechaHoraInicio(OffsetDateTime fechaHoraInicio) {
    this.fechaHoraInicio = fechaHoraInicio;
    return this;
  }

  /**
   * Get fechaHoraInicio
   * @return fechaHoraInicio
   */
  @NotNull @Valid 
  @JsonProperty("fechaHoraInicio")
  public OffsetDateTime getFechaHoraInicio() {
    return fechaHoraInicio;
  }

  public void setFechaHoraInicio(OffsetDateTime fechaHoraInicio) {
    this.fechaHoraInicio = fechaHoraInicio;
  }

  public FranjaHoraria fechaHoraFin(OffsetDateTime fechaHoraFin) {
    this.fechaHoraFin = fechaHoraFin;
    return this;
  }

  /**
   * Get fechaHoraFin
   * @return fechaHoraFin
   */
  @NotNull @Valid 
  @JsonProperty("fechaHoraFin")
  public OffsetDateTime getFechaHoraFin() {
    return fechaHoraFin;
  }

  public void setFechaHoraFin(OffsetDateTime fechaHoraFin) {
    this.fechaHoraFin = fechaHoraFin;
  }

  public FranjaHoraria profesionalOServicioId(@Nullable String profesionalOServicioId) {
    this.profesionalOServicioId = profesionalOServicioId;
    return this;
  }

  /**
   * Get profesionalOServicioId
   * @return profesionalOServicioId
   */
  
  @JsonProperty("profesionalOServicioId")
  public @Nullable String getProfesionalOServicioId() {
    return profesionalOServicioId;
  }

  public void setProfesionalOServicioId(@Nullable String profesionalOServicioId) {
    this.profesionalOServicioId = profesionalOServicioId;
  }

  public FranjaHoraria estado(EstadoEnum estado) {
    this.estado = estado;
    return this;
  }

  /**
   * Get estado
   * @return estado
   */
  @NotNull 
  @JsonProperty("estado")
  public EstadoEnum getEstado() {
    return estado;
  }

  public void setEstado(EstadoEnum estado) {
    this.estado = estado;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FranjaHoraria franjaHoraria = (FranjaHoraria) o;
    return Objects.equals(this.id, franjaHoraria.id) &&
        Objects.equals(this.fechaHoraInicio, franjaHoraria.fechaHoraInicio) &&
        Objects.equals(this.fechaHoraFin, franjaHoraria.fechaHoraFin) &&
        Objects.equals(this.profesionalOServicioId, franjaHoraria.profesionalOServicioId) &&
        Objects.equals(this.estado, franjaHoraria.estado);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, fechaHoraInicio, fechaHoraFin, profesionalOServicioId, estado);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FranjaHoraria {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    fechaHoraInicio: ").append(toIndentedString(fechaHoraInicio)).append("\n");
    sb.append("    fechaHoraFin: ").append(toIndentedString(fechaHoraFin)).append("\n");
    sb.append("    profesionalOServicioId: ").append(toIndentedString(profesionalOServicioId)).append("\n");
    sb.append("    estado: ").append(toIndentedString(estado)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

