package org.ups.citasalud.presentation.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservaCitaRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-07-04T16:04:14.475931500-05:00[America/Guayaquil]", comments = "Generator version: 7.16.0")
public class ReservaCitaRequest {

  private String franjaHorariaId;

  private String pacienteId;

  public ReservaCitaRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReservaCitaRequest(String franjaHorariaId, String pacienteId) {
    this.franjaHorariaId = franjaHorariaId;
    this.pacienteId = pacienteId;
  }

  public ReservaCitaRequest franjaHorariaId(String franjaHorariaId) {
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

  public ReservaCitaRequest pacienteId(String pacienteId) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservaCitaRequest reservaCitaRequest = (ReservaCitaRequest) o;
    return Objects.equals(this.franjaHorariaId, reservaCitaRequest.franjaHorariaId) &&
        Objects.equals(this.pacienteId, reservaCitaRequest.pacienteId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(franjaHorariaId, pacienteId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservaCitaRequest {\n");
    sb.append("    franjaHorariaId: ").append(toIndentedString(franjaHorariaId)).append("\n");
    sb.append("    pacienteId: ").append(toIndentedString(pacienteId)).append("\n");
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

