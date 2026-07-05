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
 * Cita
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-07-04T16:04:14.475931500-05:00[America/Guayaquil]", comments = "Generator version: 7.16.0")
public class Cita {

  private String id;

  private String pacienteId;

  private String franjaHorariaId;

  /**
   * Gets or Sets estado
   */
  public enum EstadoEnum {
    CONFIRMADA("CONFIRMADA");

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

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime fechaHoraCreacion;

  public Cita() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Cita(String id, String pacienteId, String franjaHorariaId, EstadoEnum estado, OffsetDateTime fechaHoraCreacion) {
    this.id = id;
    this.pacienteId = pacienteId;
    this.franjaHorariaId = franjaHorariaId;
    this.estado = estado;
    this.fechaHoraCreacion = fechaHoraCreacion;
  }

  public Cita id(String id) {
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

  public Cita pacienteId(String pacienteId) {
    this.pacienteId = pacienteId;
    return this;
  }

  /**
   * Get pacienteId
   * @return pacienteId
   */
  @NotNull 
  @JsonProperty("pacienteId")
  public String getPacienteId() {
    return pacienteId;
  }

  public void setPacienteId(String pacienteId) {
    this.pacienteId = pacienteId;
  }

  public Cita franjaHorariaId(String franjaHorariaId) {
    this.franjaHorariaId = franjaHorariaId;
    return this;
  }

  /**
   * Get franjaHorariaId
   * @return franjaHorariaId
   */
  @NotNull 
  @JsonProperty("franjaHorariaId")
  public String getFranjaHorariaId() {
    return franjaHorariaId;
  }

  public void setFranjaHorariaId(String franjaHorariaId) {
    this.franjaHorariaId = franjaHorariaId;
  }

  public Cita estado(EstadoEnum estado) {
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

  public Cita fechaHoraCreacion(OffsetDateTime fechaHoraCreacion) {
    this.fechaHoraCreacion = fechaHoraCreacion;
    return this;
  }

  /**
   * Get fechaHoraCreacion
   * @return fechaHoraCreacion
   */
  @NotNull @Valid 
  @JsonProperty("fechaHoraCreacion")
  public OffsetDateTime getFechaHoraCreacion() {
    return fechaHoraCreacion;
  }

  public void setFechaHoraCreacion(OffsetDateTime fechaHoraCreacion) {
    this.fechaHoraCreacion = fechaHoraCreacion;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Cita cita = (Cita) o;
    return Objects.equals(this.id, cita.id) &&
        Objects.equals(this.pacienteId, cita.pacienteId) &&
        Objects.equals(this.franjaHorariaId, cita.franjaHorariaId) &&
        Objects.equals(this.estado, cita.estado) &&
        Objects.equals(this.fechaHoraCreacion, cita.fechaHoraCreacion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, pacienteId, franjaHorariaId, estado, fechaHoraCreacion);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Cita {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    pacienteId: ").append(toIndentedString(pacienteId)).append("\n");
    sb.append("    franjaHorariaId: ").append(toIndentedString(franjaHorariaId)).append("\n");
    sb.append("    estado: ").append(toIndentedString(estado)).append("\n");
    sb.append("    fechaHoraCreacion: ").append(toIndentedString(fechaHoraCreacion)).append("\n");
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

